# misc package

This package contains classes and helper utilities that are *not* related to robotics.
This file should be used to document all classes, their public facing functions,
and their usage.

## Angle.java

This class is for easily converting between units of rotation. To make a new
instance, call `Angle.{UNIT}({VALUE})`. To get a value from it, get
`{INSTANCE}.{UNIT}`. To add or subtract use the methods `add(other)` and
`sub(other)`. It supports the units:

* Rotations (*0 - 1*)
* Radians (*0 - 2π*)
* Degrees (*0 - 360*)
* Grads (*0 - 100*)

> [!NOTE]
> The `add` and `sub` methods return a *new* instance of `Angle`.

## Table.java

This class creates a table of data with named fields for printing data in a
concise and readable way.

### Creation

To create a table call the constructor for the `Table` class. The constructor
accepts any number of strings as arguments which are the fields for the table.

### Configuration

All configuration options are set through methods; all of these methods are
chainable and you do not need to call all of them (*default values will be stated.*)

#### Justification

Sets the alignment mode for the table (i.e. which side of the table cell to
write the data on.) \
Method: `justify`
Accepts: any value from the `Table.Justify` enum:

* `LEFT`
* `CENTER`
* `RIGHT`

##### Head Justification

Sets the justification for just the table head (the fields). \
Default: `Table.Justify.CENTER` \
Method: `justifyHead`

##### Body Justification

Sets the justification for just the table body (the data). \
Default: `Table.Justify.LEFT`
Method: `justifyBody`

#### Border edges

Selects which edges of the border should be drawn. \
Default: `Table.Borders.MIDDLE_STOPPER` \
Method: `borders` \
Accepts:

* Any value from enum `Table.Borders`. *Please check the `Table.Borders` enum
  for possible edges and presets.*
* A combination of members from enum `Table.Borders`.
  * You can add an edge to a configuration with the format `<CURRENT>.id | <NEW>.id`.
  * You can remove an edge from a configuration with the format
    `<CURRENT>.id & NO_<NEW>.id`.

#### Border style

Sets which character set to use for the borders. \
Default: `Table.Style.ASCII` \
Method: `style` \
Accepts: any value from `Table.Style` enum:

* `ASCII` - Uses hyphens, pipes, and plus characters for borders.
* `SOLID` - Uses the `LIGHT` Unicode box drawing characters
(`U+2500`-`U+257F`). (*UTF-8 only*)
* `THICK` - Uses the `HEAVEY` Unicode box drawing characters. (*UTF-8 only*)
* `DOUBLE` - Uses the `DOUBLE` Unicode box drawing characters
(`U+2550`-`U+256C`). (*UTF-8 only*)
* `ROUNDED` - Same as `SOLID` mode but replaces corners with the `ARC` Unicode
box drawing characters (`U+256D`-`U+2570`). (*UTF-8 only*)

#### Padding

Sets the number of spaces between the sides of table cells. \
Default: `1` \
Method: `padding` \
Accepts: integer

### Adding data

To add data to the table use the `addRow` method. It accepts any number of any
type of values. These values will be (in order) added to (the same) new row on
the table. This method can be chained with itself and any configuration methods.
> [!NOTE]
> These values will be added to the table using the value returned by `<ITEM>.toString()`.

### Printing and string representation

To get the table as a string use the `toString` method. \
To print the table you can either use the `print` method or call

```java
System.out.println(TABLE.toString());
```

### Example

```java
import misc.Table;

Table T = new Table("Width (in)", "Trial 1 (m/s)", "Trial 2 (m/s)",
    "Trial 3 (m/s)", "Average (m/s)", "Kenetic Energy (J)", "Energy Effeciency (%)")

  .justifyBody(Table.Justify.RIGHT)
  .borders(Table.Borders.NO_TOP_INNER)
  .style(Table.Style.DOUBLE)
  .padding(0)

  .addRow( 0.844, 1.439, 1.444, 1.442, 1.441666667, 0.387801386, 88.41 )
  .addRow( 1.908, 1.513, 1.538, 1.53, 1.527, 0.43657785, 99.53 )
  .addRow( 2.453, 1.488, 1.456, 1.491, 1.478333333, 0.4146046065, 94.52 )
  .addRow( 2.605, 1.387, 1.435, 1.518, 1.446666667, 0.429756426, 97.97 );

T.print();
// Example of printing a bold table instead.
System.out.println("\033[1m" + T.toString() + "\033[m");
```
