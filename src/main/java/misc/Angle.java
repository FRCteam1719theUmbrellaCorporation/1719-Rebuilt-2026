package misc;

import java.lang.Math;

public class Angle {
	private static enum Unit {
		ROTATIONS(1f),
		RADIANS(2f*Math.PI),
		DEGREES(360f),
		GRADS(100f);

		public double rot;

		Unit( double full ) { this.rot = full; }
	}

	public final double rotations;
	public final double radians;
	public final double degrees;
	public final double grads;

	private Angle(double v, Unit u) {
		this.rotations = v * (Unit.ROTATIONS.rot / u.rot);
		this.radians =   v * (Unit.RADIANS.rot / u.rot);
		this.degrees =   v * (Unit.DEGREES.rot / u.rot);
		this.grads =     v * (Unit.GRADS.rot / u.rot);
	}

	public static Angle rotations( double v ) { return new Angle(v, Unit.ROTATIONS); }
	public static Angle radians( double v )   { return new Angle(v, Unit.RADIANS);   }
	public static Angle degrees( double v )   { return new Angle(v, Unit.DEGREES);   }
	public static Angle grads( double v )     { return new Angle(v, Unit.GRADS);     }

	public Angle add( Angle other ) {
		return new Angle(this.rotations + other.rotations, Unit.ROTATIONS);
	}

	public Angle sub( Angle other ) {
		return new Angle(this.rotations - other.rotations, Unit.ROTATIONS);
	}
}
