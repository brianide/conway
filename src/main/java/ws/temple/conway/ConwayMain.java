package ws.temple.conway;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import ws.temple.conway.rules.Rules;
import ws.temple.conway.rules.Rulestring;

public class ConwayMain {
	
	public static void main(String[] args) {
		
		final Config conf = ConfigFactory.load("conway.conf");
		final Rules rules = new Rulestring(conf.getString("rulestring"));
		final int tickrate = conf.getInt("tickrate");
		final int gridWidth = conf.getInt("grid.width");
		final int gridHeight = conf.getInt("grid.height");
		final int cellSize = conf.getInt("cell.size");
		final int cellBorder = conf.getInt("cell.border");
		
		SwingUtilities.invokeLater(() -> {
			final JFrame frame = new JFrame("Conway's Game of Life");
			final ConwayCanvas game = new ConwayCanvas(gridWidth, gridHeight, cellSize, cellBorder, tickrate, rules);
			
			frame.add(game);
			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setVisible(true);
			
			game.initialize();
		});
	}

}
