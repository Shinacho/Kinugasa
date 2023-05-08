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
 * �W���o�͂��g���[�X����f�o�b�O�p�̃E�C���h�E�ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_21:50:14<br>
 * @author Shinacho<br>
 */
public final class StdOutTracer {

	/**
	 * �B��̃C���X�^���X.
	 */
	private static StdOutTracer instance;
	//
	/**
	 * �\������t���[��.
	 */
	private JFrame frame;
	/**
	 * �ŏ�ʂ̃p�l��.
	 */
	private JPanel topPanel;
	/**
	 * textArea���i�[����X�N���[���y�C��.
	 */
	private JScrollPane scrollPane;
	/**
	 * �g���[�X�@�\��������e�L�X�g�G���A.
	 */
	private StreamTextArea textArea;
	/**
	 * �{�^���̃y�C��.
	 */
	private JPanel southPanel;
	/**
	 * �t�@�C�����s�{�^��.
	 */
	private JButton saveButton;
	/**
	 * �e�L�X�g�G���A�N���A�{�^��.
	 */
	private JButton clearButton;
	/**
	 * �g���[�X�E�C���h�E�N���[�Y�{�^��.
	 */
	private JButton closeButton;
	/**
	 * �V�X�e���I���{�^��.
	 */
	private JButton exitButton;

	/**
	 * �V�����E�C���h�E���쐬���܂�.
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
	 * �E�C���h�E����邽�߂̃A�N�V�����ł�.
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
	 * �V�X�e�����I�������邽�߂̃A�N�V�����ł�.
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
	 * �e�L�X�g�G���A���N���A���邽�߂̃A�N�V�����ł�.
	 */
	private ActionListener clearButtonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			textArea.setText("");
		}
	};
	/**
	 * �_���v���邽�߂̃A�N�V�����ł�.
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
		 * �w�肳�ꂽ�t�@�C���ɁA�w�肳�ꂽ��������e�L�X�g�t�@�C���Ƃ��ĕۑ����܂�.
		 *
		 * @param file �ۑ�����t�@�C�����w�肵�܂��B<br>
		 * @param text �ۑ����镶������w�肵�܂��B<br>
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
		 * �t�@�C���𔭍s����f�t�H���g�̃p�X�ł�. �f�X�N�g�b�v��LOG_[Unix����].txt�ƂȂ�܂��B
		 */
		private final String defaultPath = System.getProperty("user.home") + "/Desktop/LOG_" + System.currentTimeMillis() + ".txt";

		/**
		 * �t�@�C����ۑ����邽�߂̃`���[�U��\�����A�ۑ�����t�@�C����I�����܂�.
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
		 * ���O��ۑ�����ۂɎg�p����e�L�X�g�t�@�C���p�̃t�@�C���t�B���^�̎����ł�.
		 */
		private final FileFilter TXT_FILE_FILTER = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".txt") || f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "�e�L�X�g�t�@�C��(*.txt)";
			}
		};
	};

	/**
	 * �@�\��L�������܂�.
	 * <br>
	 * <b>���ӁFOpenGL�p�C�v���C�����g�p����ꍇ�́AStdOutTracer��L�������Ă���OpenGL��L�������܂�.</b>
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
	 * �X�g���[���̃f�[�^��\�����邽�߂̃e�L�X�g�G���A�̎����ł�.
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
		 * �Ď�����X�g���[��.
		 */
		private PrintStream stream;

		/**
		 * �V�����e�L�X�g�G���A���\�z���܂�.
		 */
		private StreamTextArea() {
			setEditable(false);
			stream = new PrintStream(new VisualStream(this));
			System.setOut(stream);
			System.setErr(stream);
		}

		/**
		 * �e�L�X�g�G���A�Ƀe�L�X�g��ǉ����܂�. ���̃��\�b�h��EDT��Ńe�L�X�g��ǉ����܂�.<Br>
		 *
		 * @param str �ǉ�����e�L�X�g.<br>
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
	 * �e�L�X�g�G���A�Ƀf�[�^�𑗐M���邽�߂̃X�g���[���̎����ł�.
	 * <br>
	 *
	 * <br>
	 *
	 * @version 1.0.0 - 2013/01/12_21:50:14<br>
	 * @author Shinacho<br>
	 */
	private static class VisualStream extends ByteArrayOutputStream {

		/**
		 * ���M��̃e�L�X�g�G���A.
		 */
		private StreamTextArea tArea;

		/**
		 * �V�����X�g���[�����\�z���܂�.
		 *
		 * @param tArea ���M��̃e�L�X�g�G���A.<Br>
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
