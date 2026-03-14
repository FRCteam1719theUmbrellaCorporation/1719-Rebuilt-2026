#!/usr/bin/env python3
"""
auto_preview_server.py — Driver station utility for FRC Team 1719
==================================================================
Watches the NetworkTables "Auto Chooser/active" key and serves the
matching PNG as a fake MJPEG stream on localhost so that Shuffleboard's
built-in Camera Stream widget can display it.

The stream is also published back to NT under /CameraPublisher/Auto Preview
so Shuffleboard discovers it automatically — no manual URL entry needed.

Requirements:
    pip install -r requirements.txt

Usage:
    python auto_preview_server.py              # defaults: team 1719, port 1186
    python auto_preview_server.py --team 9999
    python auto_preview_server.py --port 1187

Image naming:
    Place PNG files in the images/ subdirectory named exactly after the auto,
    e.g. "Center start auto.png" for an auto called "Center start auto".
    A fallback image "_no_selection.png" is shown when no match is found.
"""

import argparse
import io
import threading
import time
from http.server import BaseHTTPRequestHandler, HTTPServer
from pathlib import Path

try:
    from PIL import Image
    _PIL_AVAILABLE = True
except ImportError:
    _PIL_AVAILABLE = False
    print("WARNING: Pillow not installed — images will not be resized. "
          "Run: pip install Pillow")

import ntcore

# ---------------------------------------------------------------------------
IMAGES_DIR      = Path(__file__).parent / "images"
FALLBACK_NAME   = "_no_selection"
FRAME_INTERVAL  = 0.5   # seconds between MJPEG frame re-sends
NT_TABLE        = "SmartDashboard"
NT_KEY          = "Auto Chooser/active"
STREAM_WIDTH    = 640
STREAM_HEIGHT   = 360
# ---------------------------------------------------------------------------

_current_jpeg: bytes = b""
_frame_lock = threading.Lock()


def _load_jpeg(name: str) -> bytes:
    """Return JPEG bytes for the PNG whose stem matches *name*."""
    path = IMAGES_DIR / f"{name}.png"
    if not path.exists():
        path = IMAGES_DIR / f"{FALLBACK_NAME}.png"

    if _PIL_AVAILABLE:
        if path.exists():
            img = Image.open(path).convert("RGB")
            img = img.resize((STREAM_WIDTH, STREAM_HEIGHT), Image.LANCZOS)
        else:
            # Grey placeholder with centred text
            img = Image.new("RGB", (STREAM_WIDTH, STREAM_HEIGHT), (60, 60, 60))
        buf = io.BytesIO()
        img.save(buf, format="JPEG", quality=85)
        return buf.getvalue()
    else:
        # No Pillow: serve raw PNG bytes if available, else empty
        return path.read_bytes() if path.exists() else b""


def _nt_watcher(team: int, port: int) -> None:
    """Connect to NT, watch the auto chooser, and update _current_jpeg."""
    global _current_jpeg

    inst = ntcore.NetworkTableInstance.getDefault()
    inst.startClient4("auto-preview-server")
    inst.setServerTeam(team)

    # Publish stream URL so Shuffleboard discovers it automatically
    cam_table = inst.getTable("CameraPublisher").getSubTable("Auto Preview")
    streams_pub = cam_table.getStringArrayTopic("streams").publish()
    streams_pub.set([f"mjpeg:http://localhost:{port}/?action=stream"])
    cam_table.getStringTopic(".type").publish().set("cs-source")

    chooser_entry = inst.getTable(NT_TABLE).getStringTopic(NT_KEY).subscribe("")

    last_selection = None
    print("Waiting for NT connection…")
    while True:
        selection = chooser_entry.get()
        if selection != last_selection:
            last_selection = selection
            label = selection if selection else FALLBACK_NAME
            jpeg = _load_jpeg(label)
            with _frame_lock:
                _current_jpeg = jpeg
            print(f"Auto selected: {selection!r} → "
                  f"{'images/' + label + '.png' if (IMAGES_DIR / (label + '.png')).exists() else 'fallback'}")
        time.sleep(0.2)


class _MJPEGHandler(BaseHTTPRequestHandler):
    def log_message(self, *_args):
        pass  # suppress per-request HTTP logs

    def do_GET(self):
        if "stream" not in self.path:
            # Redirect bare requests to the stream path
            self.send_response(302)
            self.send_header("Location", "/?action=stream")
            self.end_headers()
            return

        self.send_response(200)
        self.send_header("Cache-Control", "no-cache")
        self.send_header(
            "Content-Type",
            "multipart/x-mixed-replace; boundary=frame"
        )
        self.end_headers()
        try:
            while True:
                with _frame_lock:
                    frame = _current_jpeg
                if frame:
                    self.wfile.write(b"--frame\r\n")
                    self.wfile.write(b"Content-Type: image/jpeg\r\n\r\n")
                    self.wfile.write(frame)
                    self.wfile.write(b"\r\n")
                time.sleep(FRAME_INTERVAL)
        except (BrokenPipeError, ConnectionResetError):
            pass  # client disconnected


def main():
    parser = argparse.ArgumentParser(
        description="Serve auto-preview images as an MJPEG stream for Shuffleboard"
    )
    parser.add_argument("--team", type=int, default=1719,
                        help="FRC team number (used to locate the robot's NT server)")
    parser.add_argument("--port", type=int, default=1186,
                        help="Local port to serve the MJPEG stream on (default: 1186)")
    args = parser.parse_args()

    # Pre-load fallback so the widget isn't blank before NT connects
    with _frame_lock:
        _current_jpeg = _load_jpeg(FALLBACK_NAME)

    watcher = threading.Thread(
        target=_nt_watcher, args=(args.team, args.port), daemon=True
    )
    watcher.start()

    print(f"MJPEG stream:  http://localhost:{args.port}/?action=stream")
    print(f"Images folder: {IMAGES_DIR.resolve()}")
    print("Shuffleboard should discover 'Auto Preview' automatically.")
    print("Press Ctrl+C to stop.\n")

    server = HTTPServer(("localhost", args.port), _MJPEGHandler)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopped.")


if __name__ == "__main__":
    main()
