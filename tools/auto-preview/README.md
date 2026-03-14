# Auto Preview Server

Driver station utility that watches the robot's auto chooser and displays
the matching image in Shuffleboard's Camera Stream widget.

## One-time setup (driver station laptop)

1. **Install Python 3.11+** (already present on most driver station laptops)

2. **Install dependencies:**
   ```
   cd tools/auto-preview
   pip install -r requirements.txt
   ```

3. **Add images** — see `images/README.md` for naming convention.

4. **Add the widget to Shuffleboard** — see below.

## Running before a match

```
python tools/auto-preview/auto_preview_server.py
```

The script connects to the robot's NT server, publishes the stream under
`CameraPublisher/Auto Preview`, and Shuffleboard will discover it
automatically the next time it loads or when you add a Camera Stream widget.

Optional flags:
```
--team 1719   # your team number (default: 1719)
--port 1186   # local port (default: 1186, change if in use)
```

## Adding the Camera Stream widget in Shuffleboard

1. Open Shuffleboard and navigate to the tab where you want the preview.
2. In the **Sources** panel on the left, expand **Camera** → you should see
   **Auto Preview** listed once the script is running and NT is connected.
3. Drag **Auto Preview** onto the tab. Shuffleboard creates a Camera Stream
   widget pointed at the MJPEG stream automatically.
4. Resize/reposition as desired, then **File → Save** the layout.

If the Sources panel doesn't show it yet, click the refresh icon or restart
Shuffleboard after the script is running.

## How it works

```
Robot NT ("Auto Chooser/active")
        │  ntcore
        ▼
auto_preview_server.py
  • loads images/<selection>.png
  • serves it as MJPEG on localhost:1186
  • publishes stream URL to NT /CameraPublisher/Auto Preview
        │  HTTP MJPEG
        ▼
Shuffleboard Camera Stream widget
```
