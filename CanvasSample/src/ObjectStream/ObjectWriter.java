package ObjectStream;

import canvas.graphics_option.EyeLineOption;
import canvas.graphics_option.FrontHairOption;
import canvas.graphics_option.GraphicsOption;
import file_io.FileOutputData;

public class ObjectWriter {
	public static void writeGraphicsOption( GraphicsOption option, FileOutputData outData ) {
		if( option == null ) {
			return;
		} else if( option instanceof EyeLineOption ) {
			ObjectWriter.writeEyeLineOption( ( EyeLineOption ) option, outData );
		} else if( option instanceof FrontHairOption ) {
			
		}
	}
	public static void writeEyeLineOption( EyeLineOption option, FileOutputData outData ) {
		outData.writeObject( option );
	}
	public static void writeFrontHairOption( FrontHairOption option, FileOutputData outData ) {
		
	}
}
