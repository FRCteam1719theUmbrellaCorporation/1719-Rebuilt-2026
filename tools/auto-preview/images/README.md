# Auto Preview Images

Place one PNG file here for each autonomous routine, named **exactly** after
the auto as it appears in the PathPlanner chooser.

## Naming convention

| Auto name in chooser     | Image filename               |
|--------------------------|------------------------------|
| `Center start auto`      | `Center start auto.png`      |
| `Human left basic`       | `Human left basic.png`       |
| `Human right advanced`   | `Human right advanced.png`   |
| *(no selection / unknown)* | `_no_selection.png`        |

## Recommended image size
**640 × 360 px** (16:9). The server will resize any PNG to fit, but starting
at the right size avoids quality loss.

## Generating images
PathPlanner's GUI can export a field preview — screenshot the path view for
each auto and crop/save as the filename above.
