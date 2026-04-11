// package misc;

// import java.lang.Math;
// import java.util.ArrayList;

// public final class Table {
// 	private static class BorderEdges {
// 		public int id;
// 		public BorderEdges( int id ) {
// 			this.id = id;
// 		}

// 		/* --- Checking methods --- */
// 		public boolean has_top( )			{ return (this.id & Borders.TOP.id) != 0; }
// 		public boolean has_top_sides( )		{ return (this.id & Borders.TOP_SIDES.id) != 0; }
// 		public boolean has_bottom( )		{ return (this.id & Borders.BOTTOM.id) != 0; }
// 		public boolean has_bottom_sides( )	{ return (this.id & Borders.BOTTOM_SIDES.id) != 0; }
// 		public boolean has_middle( )		{ return (this.id & Borders.MIDDLE.id) != 0; }
// 		public boolean has_top_inner( )		{ return (this.id & Borders.TOP_INNER.id) != 0; }
// 		public boolean has_bottom_inner( )	{ return (this.id & Borders.BOTTOM_INNER.id) != 0; }
// 	}

// 	/* === Enums === */
// 	public static enum Justify {
// 		LEFT,
// 		CENTER,
// 		RIGHT,
// 	}

// 	// Enum for border styles
// 	// Contains multiple resets and individual sides
// 	// To add a new `<BORDER>` to the `<CURRENT>` border style, use the format `<CURRENT>.id | <BORDER>.id`
// 	// To remove a `<BORDER>` from the <CURRENT>` border style, use the format `<CURRENT>.id & NO_<BORDER>.id`
// 	public static enum Borders {
// 		/* --- Base Values --- */
// 		NONE			( 0b0_000_000 ),	ALL				( ~0b0_000_000 ),
// 		TOP				( 0b0_000_001 ),	NO_TOP			( ~0b0_000_001 ),
// 		TOP_SIDES		( 0b0_000_010 ),	NO_TOP_SIDES	( ~0b0_000_010 ),
// 		TOP_INNER		( 0b0_000_100 ),	NO_TOP_INNER	( ~0b0_000_100 ),
// 		BOTTOM			( 0b0_001_000 ),	NO_BOTTOM		( ~0b0_001_000 ),
// 		BOTTOM_SIDES	( 0b0_010_000 ),	NO_BOTTOM_SIDES	( ~0b0_010_000 ),
// 		BOTTOM_INNER	( 0b0_100_000 ),	NO_BOTTOM_INNER	( ~0b0_100_000 ),
// 		MIDDLE			( 0b1_000_000 ),	NO_MIDDLE		( ~0b1_000_000 ),
		
// 		/* --- Presets --- */
// 		// Only vertical bars
// 		INNER			( Borders.BOTTOM_INNER.id | Borders.TOP_INNER.id ),
// 		// Only border around the head
// 		HEAD			( Borders.TOP.id | Borders.TOP_SIDES.id | Borders.MIDDLE.id | Borders.TOP_INNER.id ),
// 		// Middle line and a line at the bottom
// 		MIDDLE_STOPPER	( Borders.MIDDLE.id | Borders.BOTTOM.id ),
// 		// Head highlighted and a line at the bottom
// 		HEAD_STOPPER	( Borders.HEAD.id | Borders.BOTTOM.id ),
// 		// Only border around the body
// 		BODY			( Borders.BOTTOM.id | Borders.BOTTOM_SIDES.id | Borders.MIDDLE.id | Borders.BOTTOM_INNER.id ),
// 		// All horizontal lines
// 		HORIZONTAL		( Borders.TOP.id | Borders.MIDDLE.id | Borders.BOTTOM.id ),
// 		// All lines except the ends
// 		NO_ENDS			( Borders.ALL.id & Borders.NO_BOTTOM.id & Borders.NO_TOP.id );

// 		public int id;
// 		private BorderEdges be;
// 		Borders( int id ) {
// 			this.id = id;
// 			this.be = new BorderEdges(id);
// 		}
// 	}

// 	// Different border styles
// 	public static enum Style {
// 		// '  - | + + + + + + + + +'
// 		ASCII		(new char[]{ ' ', '-', '|', '+', '+', '+', '+', '+', '+', '+', '+', '+' }),
// 		// '  ─ │ ┌ ┐ └ ┘ ├ ┤ ┬ ┴ ┼'
// 		SOLID		(new char[]{ ' ', '\u2500', '\u2502', '\u250C', '\u2510', '\u2514', '\u2518', '\u251C', '\u2524', '\u252C', '\u2534', '\u253C' }),
// 		// '  ━ ┃ ┏ ┓ ┗ ┛ ┣ ┫ ┳ ┻ ╋'
// 		THICK		(new char[]{ ' ', '\u2501', '\u2503', '\u250F', '\u2513', '\u2517', '\u251B', '\u2523', '\u252B', '\u2533', '\u253B', '\u254B' }),
// 		// '  ═ ║ ╔ ╗ ╚ ╝ ╠ ╣ ╦ ╩ ╬'
// 		DOUBLE		(new char[]{ ' ', '\u2550', '\u2551', '\u2554', '\u2557', '\u255A', '\u255D', '\u2560', '\u2563', '\u2566', '\u2569', '\u256C' }),
// 		// '  ─ │ ╭ ╮ ╰ ╯ ├ ┤ ┬ ┴ ┼'
// 		ROUNDED		(new char[]{ ' ', '\u2500', '\u2502', '\u250C', '\u256D', '\u256E', '\u256F', '\u2570', '\u2524', '\u252C', '\u2534', '\u253C' });

// 		private char[] chars;
// 		Style( char[] chars ) {
// 			this.chars = chars;
// 		}
// 	}

// 	// Each member of this enum represents an index to the table's current `_style`
// 	private static enum Chars {
// 		EMPTY,		// Space
// 		HORIZONTAL,	// Horizontal line
// 		VERTICAL,	// Vertical line
// 		TOP_LEFT,	// Top-left corner
// 		TOP_RIGHT,	// Top-right corner
// 		BACK_LEFT,	// Bottom-left corner
// 		BACK_RIGHT,	// Bottom-right corner
// 		LEFT,		// T-shape with flat side facing left
// 		RIGHT,		// T-shape with flat side facing right
// 		TOP,		// T-shape with flat side facing the top
// 		BOTTOM,		// T-shape with flat side facing the bottom
// 		CENTER,		// Plus
// 	}

// 	/* === Public Static Functions === */
// 	// Code to pad a string to the left, right, or center justification with a set length and pad character
// 	// Default pad character is ' '
// 	// Default justification is Justify.LEFT
// 	public static String pad( String input, int length ) { return Table.pad(input, length, ' ', Justify.LEFT); }
// 	public static String pad( String input, int length, char pad ) { return Table.pad(input, length, pad, Justify.LEFT); }
// 	public static String pad( String input, int length, Justify justify ) { return Table.pad(input, length, ' ', justify); }
// 	public static String pad( String input, int length, char pad, Justify justify ) {
// 		int padLength = length - input.length();
// 		if (padLength < 0) return input;

// 		switch (justify) {
// 			case LEFT:
// 				return input + Character.toString(pad).repeat(padLength);
// 			case CENTER:
// 				int padStart = padLength / 2;
// 				int padEnd = padStart + (padLength % 2);
// 				return Character.toString(pad).repeat(padStart) + input + Character.toString(pad).repeat(padEnd);
// 			case RIGHT:
// 				return Character.toString(pad).repeat(padLength) + input;
// 			default:
// 				return input;
// 		}
// 	}

// 	// Constructor
// 	public Table( String... fields ) {
// 		for ( String field : fields ) {
// 			this.fields.add(field);
// 		}
// 	}

// 	/* === Public Properties === */
// 	// List of fields for the table
// 	public ArrayList<String> fields = new ArrayList<>();
// 	// List of rows of data in string form
// 	public ArrayList<ArrayList<String>> data = new ArrayList<>();
	
// 	/* === Private Properties === */
// 	/* --- Stylization options for outputted table --- */
// 	private Justify _justifyHead = Justify.CENTER;
// 	private Justify _justifyBody = Justify.LEFT;
// 	private BorderEdges _borders = Borders.MIDDLE_STOPPER.be;
// 	private Style _style = Style.ASCII;
// 	private int _padding = 1;

// 	/* --- Setters --- */
// 	public Table justifyHead( Justify justify ) {
// 		this._justifyHead = justify;
// 		return this;
// 	}
	
// 	public Table justifyBody( Justify justify ) {
// 		this._justifyBody = justify;
// 		return this;
// 	}

// 	public Table justify( Justify justify ) {
// 		this._justifyHead = this._justifyBody = justify;
// 		return this;
// 	}

// 	public Table borders( Borders borders ) {
// 		this._borders = borders.be;
// 		return this;
// 	}
// 	public Table borders( int borders ) {
// 		this._borders = new Table.BorderEdges(borders);
// 		return this;
// 	}

// 	public Table style( Style style ) {
// 		this._style = style;
// 		return this;
// 	}

// 	public Table padding( int padding ) {
// 		this._padding = padding;
// 		return this;
// 	}

// 	/* --- Getters --- */
// 	// Retrieves a character with given index or Chars enum member
// 	private String getChar( Chars index ) { return this.getChar(index.ordinal()); }
// 	private String getChar( int index ) {
// 		return Character.toString(this._style.chars[index]);
// 	}

// 	// Returns n spaces where n is the set padding number
// 	private String getPadString( ) {
// 		return " ".repeat(this._padding);
// 	}

// 	// Gets the minimum width to fit all rows of data for a given column
// 	private int getColumnWidth( int colIndex ) {
// 		int minWidth = this.fields.get(colIndex).length();
// 		for ( ArrayList<String> row : this.data ) {
// 			minWidth = Math.max(minWidth, row.get(colIndex).length());
// 		}

// 		return minWidth + 2*this._padding;
// 	}

// 	/* === Public Methods === */
// 	// Adds a row of data to the table, returns the table for chaining
// 	public Table addRow( Object... items ) {
// 		ArrayList<String> newRow = new ArrayList<>();
// 		for ( Object item : items ) {
// 			newRow.add(item.toString());
// 		}
// 		this.data.add(newRow);

// 		return this;
// 	}

// 	// Gets the table for printing
// 	@Override
// 	public String toString( ) {
// 		String str = "";

// 		/* --- Head --- */
// 		/* ~~~ Top row ~~~ */
// 		if (this._borders.has_top()) {
// 			str += this._borders.has_top_sides() ? this.getChar(Chars.TOP_LEFT) : this.getChar(Chars.EMPTY);
// 			for ( int i = 0 ; i < this.fields.size() ; i++ ) {
// 				str += this.getChar(Chars.HORIZONTAL).repeat(this.getColumnWidth(i));

// 				if (i > this.fields.size() - 2) continue;

// 				if (this._borders.has_top_inner())			{ str += this.getChar(Chars.TOP); }
// 				else										{ str += this.getChar(Chars.HORIZONTAL); }
// 			}
// 			str += this._borders.has_top_sides() ? this.getChar(Chars.TOP_RIGHT) : this.getChar(Chars.EMPTY);
// 			str += '\n';
// 		}

// 		/* ~~~ Fields ~~~ */
// 		str += this._borders.has_top_sides() ? this.getChar(Chars.VERTICAL) : this.getChar(Chars.EMPTY);
// 		for ( int i = 0 ; i < this.fields.size() ; i++ ) {
// 			String paddedFieldName = pad(this.fields.get(i), (this.getColumnWidth(i) - 2*this._padding), ' ', this._justifyHead);
// 			str += this.getPadString() + paddedFieldName + this.getPadString();

// 			if (i > this.fields.size() - 2) continue;

// 			str += (this._borders.has_top_inner()) ? this.getChar(Chars.VERTICAL) : this.getChar(Chars.EMPTY);
// 		}
// 		str += this._borders.has_top_sides() ? this.getChar(Chars.VERTICAL) : this.getChar(Chars.EMPTY);
// 		str += '\n';

// 		/* ~~~ Field/data split line ~~~ */
// 		if (this._borders.has_middle()) {
// 			if (this._borders.has_top_sides() && this._borders.has_bottom_sides())	{ str += this.getChar(Chars.LEFT); }
// 			else if (this._borders.has_top_sides())									{ str += this.getChar(Chars.BACK_LEFT); }
// 			else if (this._borders.has_bottom_sides())								{ str += this.getChar(Chars.TOP_LEFT); }
// 			else																	{ str += this.getChar(Chars.EMPTY); }

// 			for ( int i = 0 ; i < this.fields.size() ; i++ ) {
// 				str += this.getChar(Chars.HORIZONTAL).repeat(this.getColumnWidth(i));

// 				if (i > this.fields.size() - 2) continue;

// 				if (this._borders.has_top_inner() && this._borders.has_bottom_inner())	{ str += this.getChar(Chars.CENTER); }
// 				else if (this._borders.has_top_inner())									{ str += this.getChar(Chars.BOTTOM); }
// 				else if (this._borders.has_bottom_inner())								{ str += this.getChar(Chars.TOP); }
// 				else																	{ str += this.getChar(Chars.HORIZONTAL); }
// 			}
			
// 			if (this._borders.has_top_sides() && this._borders.has_bottom_sides())	{ str += this.getChar(Chars.RIGHT); }
// 			else if (this._borders.has_top_sides())									{ str += this.getChar(Chars.BACK_RIGHT); }
// 			else if (this._borders.has_bottom_sides())								{ str += this.getChar(Chars.TOP_RIGHT); }
// 			else																	{ str += this.getChar(Chars.EMPTY); }
// 			str += '\n';
// 		}


// 		/* --- Body --- */
// 		/* ~~~ Rows ~~~ */
// 		for ( ArrayList<String> row : this.data ) {
// 			str += this._borders.has_bottom_sides() ? this.getChar(Chars.VERTICAL) : this.getChar(Chars.EMPTY);
// 			for ( int i = 0 ; i < this.fields.size() ; i++ ) {
// 				String paddedFieldName = pad(row.get(i), (this.getColumnWidth(i) - 2*this._padding), ' ', this._justifyBody);
// 				str += this.getPadString() + paddedFieldName + this.getPadString();

// 				if (i > this.fields.size() - 2) continue;

// 				str += (this._borders.has_bottom_inner()) ? this.getChar(Chars.VERTICAL) : this.getChar(Chars.EMPTY);
// 			}
// 			str += this._borders.has_bottom_sides() ? this.getChar(Chars.VERTICAL) : this.getChar(Chars.EMPTY);
// 			str += '\n';
// 		}

// 		/* ~~~ Bottom row ~~~ */
// 		if (this._borders.has_bottom()) {
// 			str += this._borders.has_bottom_sides() ? this.getChar(Chars.BACK_LEFT) : this.getChar(Chars.EMPTY);
// 			for ( int i = 0 ; i < this.fields.size() ; i++ ) {
// 				str += this.getChar(Chars.HORIZONTAL).repeat(this.getColumnWidth(i));

// 				if (i > this.fields.size() - 2) continue;

// 				if (this._borders.has_bottom_inner())	{ str += this.getChar(Chars.BOTTOM); }
// 				else									{ str += this.getChar(Chars.HORIZONTAL); }
// 			}
// 			str += this._borders.has_bottom_sides() ? this.getChar(Chars.BACK_RIGHT) : this.getChar(Chars.EMPTY);
// 			str += '\n';
// 		}


// 		return str;
// 	}

// 	// Prints the table to stdout
// 	public void print( ) {
// 		System.out.println(this.toString());
// 	}
// }
