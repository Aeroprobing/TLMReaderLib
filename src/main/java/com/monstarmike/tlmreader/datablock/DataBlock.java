package com.monstarmike.tlmreader.datablock;

import com.google.common.primitives.Ints;
import com.monstarmike.tlmreader.Flight;

public abstract class DataBlock extends Block {

	public int get_timestamp() {
		return Ints.fromBytes(this.rawData[3], this.rawData[2], this.rawData[1], this.rawData[0]);
	}

	public DataBlock(byte[] rawData) {
		super(rawData);
	}

	public static DataBlock createDataBlock(byte[] bytes, Flight currentFlight) {
		DataBlock block = null;
		short tm1100 = (short) (bytes[4] & 0xFF);
		if (tm1100 == 0xFE || tm1100 == 0xFF) {
			switch (tm1100) {
			case 0xFE:
				block = new StandardBlock(bytes, currentFlight.getRpmHeader());
				break;

			case 0xFF:
				block = new RXBlock(bytes);
				break;
			}
		} else if (bytes[4] == 0x09 && bytes[5] == 0x06) {
			block = new ServoDataBlock(bytes);
		} else {
			switch (bytes[4]) {
			case 0:
				// unknown
				break;

			case 0x01:
				// voltmeter - appears to be legacy.
				break;

			case 0x02:
				// temperature - appears to be legacy.
				break;

			case 0x03:
				// current (amperage) usage
				block = new CurrentBlock(bytes);
				break;

			case 0x0a:
				// powerbox
				block = new PowerboxBlock(bytes);
				break;

			case 0x11:
				// airspeed
				block = new AirspeedBlock(bytes);
				break;

			case 0x12:
				// altitude
				block = new AltitudeBlock(bytes);
				break;

			case 0x14:
				// g-force
				block = new GForceBlock(bytes);
				break;
				
			case 0x15:
				// JetCat
				block = new JetCat(bytes);
				break;
				
			case 0x16:
				// GPS Location Data
				block = new GPSLocationBlock(bytes);
				break;
				
			case 0x17:
				// GPS Status
				block = new GPSStatusBlock(bytes);
				break;
				
			case 0x40:
				// Vario
				block = new VarioBlock(bytes);
				break;

			case 0x7E:
				// rpm, temperature, rx volts
				block = new StandardBlock(bytes, currentFlight.getRpmHeader());
				break;

			case 0x7F:
				// signal info
				block = new RXBlock(bytes);
				break;
			}
		}
		return block;
	}

	@Override
	public int getSize() {
		return 20;
	}
}
