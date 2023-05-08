/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.util;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import kinugasa.game.GameLog;

/**
 * 標準出力をトレースするデバッグ用のウインドウです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_21:50:14<br>
 * @author Shinacho<br>
 */
public final class StdOutTracer {

	/**
	 * 唯一のインスタンス.
	 */
	private static StdOutTracer instance;
	//
	/**
	 * 表示するフレーム.
	 */
	private JFrame frame;
	/**
	 * 最上位のパネル.
	 */
	private JPanel topPanel;
	/**
	 * textAreaを格納するスクロールペイン.
	 */
	private JScrollPane scrollPane;
	/**
	 * トレース機能を備えたテキストエリア.
	 */
	private StreamTextArea textArea;
	/**
	 * ボタンのペイン.
	 */
	private JPanel southPanel;
	/**
	 * ファイル発行ボタン.
	 */
	private JButton saveButton;
	/**
	 * テキストエリアクリアボタン.
	 */
	private JButton clearButton;
	/**
	 * トレースウインドウクローズボタン.
	 */
	private JButton closeButton;
	/**
	 * システム終了ボタン.
	 */
	private JButton exitButton;

	/**
	 * 新しいウインドウを作成します.
	 */
	private StdOutTracer() {
		frame = new JFrame("stdout/stderr");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setBounds(0, 0, 512, 680);

		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		scrollPane = new JScrollPane();
		textArea = new StreamTextArea();
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		scrollPane.setViewportView(textArea);

		southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));

		saveButton = new JButton("save");
		saveButton.addActionListener(saveButtonActionListener);
		clearButton = new JButton("clear");
		clearButton.addActionListener(clearButtonActionListener);
		closeButton = new JButton("close");
		closeButton.addActionListener(closeButtonActionListener);
		exitButton = new JButton("exit");
		exitButton.addActionListener(exitButtonActionListener);

		southPanel.add(saveButton);
		southPanel.add(clearButton);
		southPanel.add(closeButton);
		southPanel.add(exitButton);

		topPanel.add("Center", scrollPane);
		topPanel.add("South", southPanel);

		frame.add(topPanel);
	}
	/**
	 * ウインドウを閉じるためのアクションです.
	 */
	private ActionListener closeButtonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.dispose();
				}
			});
		}
	};
	/**
	 * システムを終了させるためのアクションです.
	 */
	private ActionListener exitButtonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int result = JOptionPane.showConfirmDialog(frame, "System.exit(0)", "exit?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	};
	/**
	 * テキストエリアをクリアするためのアクションです.
	 */
	private ActionListener clearButtonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			textArea.setText("");
		}
	};
	/**
	 * ダンプするためのアクションです.
	 */
	private ActionListener saveButtonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String text = textArea.getText().replaceAll("\n", System.getProperty("line.separator"));
			File file = selectFile();
			if (file != null) {
				save(file, text);
			}
		}

		/**
		 * 指定されたファイルに、指定された文字列をテキストファイルとして保存します.
		 *
		 * @param file 保存するファイルを指定します。<br>
		 * @param text 保存する文字列を指定します。<br>
		 */
		private void save(File file, String text) {
			FileWriter fr = null;
			try {
				fr = new FileWriter(file);
				BufferedWriter br = new BufferedWriter(fr);
				br.write(text, 0, text.length());
				br.close();
			} catch (IOException ex) {
				JOptionPane.showConfirmDialog(frame, ex, "IOException", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			} finally {
				try {
					if (fr != null) {
						fr.close();
					}
				} catch (IOException ex) {
					JOptionPane.showConfirmDialog(frame, ex, "IOException", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		/**
		 * ファイルを発行するデフォルトのパスです. デスクトップのLOG_[Unix時刻].txtとなります。
		 */
		private final String defaultPath = System.getProperty("user.home") + "/Desktop/LOG_" + System.currentTimeMillis() + ".txt";

		/**
		 * ファイルを保存するためのチューザを表示し、保存するファイルを選択します.
		 */
		private File selectFile() {
			File defaultFile = new File(defaultPath);
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(defaultFile);
			chooser.setFileFilter(TXT_FILE_FILTER);
			int rc = chooser.showSaveDialog(frame);
			if (rc != JFileChooser.APPROVE_OPTION) {
				return null;
			}
			return chooser.getSelectedFile();
		}
		/**
		 * ログを保存する際に使用するテキストファイル用のファイルフィルタの実装です.
		 */
		private final FileFilter TXT_FILE_FILTER = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".txt") || f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "テキストファイル(*.txt)";
			}
		};
	};

	/**
	 * 機能を有効化します.
	 * <br>
	 * <b>注意：OpenGLパイプラインを使用する場合は、StdOutTracerを有効化してからOpenGLを有効化します.</b>
	 */
	public static void use() {
		if (instance == null) {
			instance = new StdOutTracer();
		}
		if (instance.frame.isVisible()) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				instance.frame.setVisible(true);
			}
		});
		GameLog.printInfo("stdout trace is enable");
	}

	public static void close() {
		if (instance == null) {
			use();
		}
		instance.closeButton.doClick();
		GameLog.printInfo("stdout trace is disable");
	}

	/**
	 * ストリームのデータを表示するためのテキストエリアの実装です.
	 * <br>
	 *
	 * <br>
	 *
	 * @version 1.0.0 - 2013/01/12_21:50:14<br>
	 * @author Shinacho<br>
	 */
	private static class StreamTextArea extends JTextArea {

		private static final long serialVersionUID = -978306416238062009L;
		/**
		 * 監視するストリーム.
		 */
		private PrintStream stream;

		/**
		 * 新しいテキストエリアを構築します.
		 */
		private StreamTextArea() {
			setEditable(false);
			stream = new PrintStream(new VisualStream(this));
			System.setOut(stream);
			System.setErr(stream);
		}

		/**
		 * テキストエリアにテキストを追加します. このメソッドはEDT上でテキストを追加します.<Br>
		 *
		 * @param str 追加するテキスト.<br>
		 */
		public void addText(final String str) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					append(str);
				}
			});
		}
	}

	/**
	 * テキストエリアにデータを送信するためのストリームの実装です.
	 * <br>
	 *
	 * <br>
	 *
	 * @version 1.0.0 - 2013/01/12_21:50:14<br>
	 * @author Shinacho<br>
	 */
	private static class VisualStream extends ByteArrayOutputStream {

		/**
		 * 送信先のテキストエリア.
		 */
		private StreamTextArea tArea;

		/**
		 * 新しいストリームを構築します.
		 *
		 * @param tArea 送信先のテキストエリア.<Br>
		 */
		private VisualStream(StreamTextArea tArea) {
			this.tArea = tArea;
		}

		@Override
		public void write(byte[] b) throws IOException {
			super.write(b);
			tArea.addText(this.toString());
			reset();
		}

		@Override
		public synchronized void write(int b) {
			super.write(b);
			tArea.addText(this.toString());
			reset();
		}

		@Override
		public synchronized void write(byte[] b, int off, int len) {
			super.write(b, off, len);
			tArea.addText(this.toString());
			reset();
		}
	}

	public static void main(String[] args) throws Exception {
		
		try{
			throw new Exception();
		}catch(Exception ex){
			System.out.println(ex.getStackTrace()[0].getClassName());
		}
		

	}
}
