package ws.temple.conway;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.Arrays;

import javax.swing.Timer;

import ws.temple.conway.rules.Rules;
import ws.temple.conway.rules.Rulestring;

public class ConwayCanvas extends Canvas {
	
	private static final long serialVersionUID = 6305046550861536527L;
	
	/* Grid dimensions in cells */
	private final int gridWidth;
	private final int gridHeight;
	
	/* Size of each cell in pixels */
	private final int cellSize;
	
	/* Inset gap between cells */
	private final int cellBorderwidth;
	
	/* Desired rate at which to run the simulation */
	private final int tickrate;
	
	/* Grid containing the state of the simulation */
	private boolean[][] grid;
	
	/* Buffers for calculating the next generation without the use of an
	 * entire second state grid */
	private boolean[][] lineBuffers;
	
	/* Variables for keyboard controls */
	private boolean paused = true;
	private boolean doStep = false;
	
	/* The laws governing life and death */
	private Rules rules;

	
	public ConwayCanvas(int gridWidth, int gridHeight, int cellSize, int cellBorderwidth, int tickrate, Rules rules) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.cellSize = cellSize;
		this.cellBorderwidth = cellBorderwidth;
		this.tickrate = tickrate;
		this.rules = rules;
		
		grid = new boolean[gridHeight][gridWidth];
		lineBuffers = new boolean[2][gridWidth];
		
		createMouseHandler();
		createKeyHandler();
		
		setPreferredSize(new Dimension(gridWidth * cellSize, gridHeight * cellSize));
	}

	public ConwayCanvas() {
		this(100, 100, 3, 1, 30, new Rulestring("3/23"));
	}
	
	/**
	 * Initializes the canvas for rendering and begins the simulation. Should
	 * only be called after the canvas has been made visible.
	 */
	public void initialize() {
		createBufferStrategy(2);
		new Timer(1000 / tickrate, (e) -> {
			if(!paused) {
				tick();
			}
			else if(doStep) {
				tick();
				doStep = false;
			}
			render();
		}).start();
	}
	
	/**
	 * Sets every cell in the grid to the dead (off) state.
	 */
	public void clearGrid() {
		synchronized (grid) {
			for(boolean[] row : grid) {
				Arrays.fill(row, false);
			}
		}
	}
	
	/**
	 * Setup handling for mouse-drawing on the grid.
	 */
	private void createMouseHandler() {
		final MouseAdapter adapter = new MouseAdapter() {

			private boolean drawState = false;
			
			private void setState(MouseEvent e) {
				final int row = e.getY() / cellSize;
				final int col = e.getX() / cellSize;
				if(row >= 0 && row < gridHeight && col >= 0 && col < gridWidth) {
					synchronized (grid) {
						grid[row][col] = drawState;
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				drawState = (e.getButton() == MouseEvent.BUTTON1);
				setState(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				setState(e);
			}
		};
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}
	
	/**
	 * Setup handling for keyboard commands.
	 */
	private void createKeyHandler() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				
				case KeyEvent.VK_SPACE:
					paused = !paused;
					doStep = false;
					break;
					
				case KeyEvent.VK_PERIOD:
				case KeyEvent.VK_DECIMAL:
					paused = true;
					doStep = true;
					break;
					
				case KeyEvent.VK_BACK_SPACE:
					clearGrid();
					break;
				
				}
			}
		});
	}
	
	/**
	 * Advance the simulation one generation.
	 */
	private void tick() {
		int lineBufferIndex = 0;
		
		synchronized (grid) {
			for(int i = 0; i < gridHeight; i++) {
				for(int j = 0; j < gridWidth; j++) {
					final int neighbors = countNeighbors(i, j);
					if(grid[i][j] && !rules.checkSurvival(neighbors)) {
						lineBuffers[lineBufferIndex][j] = false;
					}
					else if(!grid[i][j] && rules.checkBirth(neighbors)) {
						lineBuffers[lineBufferIndex][j] = true;
					}
					else {
						lineBuffers[lineBufferIndex][j] = grid[i][j];
					}
				}
				
				// Flip the active line buffer
				lineBufferIndex = 1 - lineBufferIndex;
				
				// After the second loop, we start writing line buffers back
				if(i > 0) {
					swapLineBuffer(i - 1, lineBufferIndex);
				}
			}
			
			// Write back the final line
			swapLineBuffer(gridHeight - 1, 1 - lineBufferIndex);
		}
	}
	
	/**
	 * Swap the given grid row with the given line buffer.
	 * 
	 * @param gridRow
	 * @param lineBufferIndex
	 */
	private void swapLineBuffer(int gridRow, int lineBufferIndex) {
		final boolean[] temp = grid[gridRow];
		grid[gridRow] = lineBuffers[lineBufferIndex];
		lineBuffers[lineBufferIndex] = temp;
	}
	
	/**
	 * Draw the current state of the simulation to the canvas.
	 */
	private void render() {
		final BufferStrategy strat = getBufferStrategy();
		final Graphics g = strat.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		synchronized (grid) {
			for(int i = 0; i < gridHeight; i++) {
				for(int j = 0; j < gridWidth; j++) {
					if(grid[i][j]) {
						g.fillRect(j * cellSize, i * cellSize, cellSize - cellBorderwidth, cellSize - cellBorderwidth);
					}
				}
			}
		}
		
		g.dispose();
		strat.show();
	}
	
	/**
	 * Returns the number of living neighbor cells to the given index.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private int countNeighbors(int row, int col) {
		int count = 0;
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j += (i == 0 ? 2 : 1)) {
				final int nrow = row - i;
				final int ncol = col - j;
				if(nrow >= 0 && nrow < gridHeight && ncol >= 0 && ncol < gridWidth && grid[nrow][ncol]) {
					count += 1;
				}
			}
		}
		return count;
	}
	
}
