// Make all function background colors contrast with function bodies via lighter if dark theme, and darker if light theme.
//@category Color

import java.awt.*; // Color
import javax.swing.JFrame;

import ghidra.app.script.GhidraScript;
import ghidra.app.plugin.core.colorizer.ColorizingService;
import ghidra.program.model.listing.*;
import ghidra.program.model.address.Address;
import ghidra.program.model.symbol.SymbolIterator;
import ghidra.framework.plugintool.PluginTool;


public class HighlightFunctions extends GhidraScript {

    public void setBackgroundColor(Address address) {
        // https://github.com/NationalSecurityAgency/ghidra/blob/195abea7e4a4a81a31077e90026e2f4ea0c30c40/Ghidra/Features/Base/src/main/java/ghidra/app/script/GhidraScript.java#L137
		    PluginTool tool = state.getTool();
		    ColorizingService service = tool.getService(ColorizingService.class);

        // Set color
        //  Get the main GUI window of Ghidra
        JFrame mainFrame = state.getTool().getToolFrame();

        //  Get the background color
        Color bgColor = mainFrame.getContentPane().getBackground();

        Color highlightColor;
        //  Theme specific logic
        if (bgColor.getRed() > 150 && bgColor.getBlue() > 150 && bgColor.getGreen() > 150) {
            // If light colors, make darker
            //  Retone to brighter again because dark doesn't mesh well with font colors
            highlightColor = bgColor.darker().brighter();
        } else {
            // if dark colors make brighter
            highlightColor = bgColor.brighter();
        }
        
		if (service == null) {
			printerr("Cannot set background colors without the " +
				ColorizingService.class.getSimpleName() + " installed");
			return;
		}

		service.setBackgroundColor(address, address, highlightColor);
	}

    @Override
    protected void run() throws Exception {
        // Get the current program
        Program program = currentProgram;
        // Get the listing of the program
        Listing listing = program.getListing();

        // Iterate through all functions in the current program
        for (Function function : program.getFunctionManager().getFunctions(true)) {
            // println("Setting Function " + function.getName() +" color.");
            // Get the entry point of the function
            Address entryPoint = function.getEntryPoint();

            if (entryPoint == null) {
                return;
            }

            // Set the color of the first instruction
            setBackgroundColor(entryPoint);

        }
    }
}
