/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game.ui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import kinugasa.game.GameOption;
import kinugasa.game.LockUtil;
import kinugasa.game.input.Keys;
import kinugasa.graphics.RenderingQuality;

/**
 *
 * @author owner
 */
public class GameLauncher extends javax.swing.JFrame {

	/**
	 * Creates new form GameOptionFrame
	 */
	public GameLauncher(String name) {
		initComponents();
		init(name);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        windowSize = new javax.swing.JComboBox<>();
        logFile = new javax.swing.JTextField();
        mouse = new javax.swing.JCheckBox();
        keyboard = new javax.swing.JCheckBox();
        gamepad = new javax.swing.JCheckBox();
        language = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        debugMode = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        args = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("オプション");
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        setResizable(false);

        jLabel1.setText("ウインドウサイズ");

        jToolBar1.setRollover(true);

        jLabel3.setText("ログファイル");

        jLabel4.setText("入力デバイス");

        jLabel7.setText("言語/Language");

        windowSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "720/480", "1080/720", "1440/960" }));
        windowSize.setSelectedIndex(2);
        windowSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                windowSizeActionPerformed(evt);
            }
        });

        logFile.setEditable(false);
        logFile.setToolTipText("ログファイルの格納場所を指定します。ログファイルは、ゲームの終了後消しても平気です。");
        logFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logFileMouseClicked(evt);
            }
        });
        logFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logFileActionPerformed(evt);
            }
        });

        mouse.setText("マウス");

        keyboard.setSelected(true);
        keyboard.setText("キーボード");

        gamepad.setSelected(true);
        gamepad.setText("コントローラー");
        gamepad.setToolTipText("コントローラを使用しない場合でもOKのままで構いませんが、OFFにすると少しパフォーマンスが改善します");

        jButton1.setFont(new java.awt.Font("MS UI Gothic", 0, 24)); // NOI18N
        jButton1.setText("起動");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        debugMode.setText("デバッグモードを有効にする");

        jLabel2.setText("ARGS：");

        args.setToolTipText("特別な引数がある場合、ここに半角スペース区切りで入力します。");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(args))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mouse, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(keyboard, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gamepad, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(debugMode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(language, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(logFile))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(windowSize, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(windowSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(logFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(mouse)
                    .addComponent(keyboard)
                    .addComponent(gamepad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(language, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(debugMode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(args, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

		if (!keyboard.isSelected() && !mouse.isSelected() && !gamepad.isSelected()) {
			JOptionPane.showConfirmDialog(null, "いずれかの入力デバイスは必要です", "起動失敗", JOptionPane.DEFAULT_OPTION);
			return;
		}
		option = new GameOption(getTitle());

		String a = args.getText().trim().replaceAll(" ", "/");
		while (a.contains("//")) {
			a = a.replaceAll("//", "/");
		}
		String[] aa = a.contains("/") ? a.split("/") : new String[]{a};
		option.setArgs(aa);

		String windowSizeStr = windowSize.getSelectedItem().toString();
		int w = Integer.parseInt(windowSizeStr.split("/")[0]);
		int h = Integer.parseInt(windowSizeStr.split("/")[1]);
		option.setWindowSize(new Dimension(w, h));
		switch (w) {
			case 720:
				option.setDrawSize(1);
				break;
			case 1080:
				option.setDrawSize(1.5f);
				break;
			case 1440:
				option.setDrawSize(2);
				break;
			default:
				throw new AssertionError();
		}

		option.setWindowLocation(new Point(0, 0));
		option.setCenterOfScreen();

		if (!logFile.getText().equals("")) {
			String logPath = new File(logFile.getText()).getParentFile().getAbsolutePath();
			String logName = new File(logFile.getText()).getName();
			option.setLogPath(logPath.replaceAll("\\\\", "/") + "/");
			option.setLogName(logName);
			option.setUseLog(true);
		}

		option.setUseMouse(mouse.isSelected());
		option.setUseKeyboard(keyboard.isSelected());
		option.setUseGamePad(gamepad.isSelected());

		option.setFps(60);

		option.setRenderingQuality(RenderingQuality.SPEED);

		String lang = language.getSelectedItem().toString();
		assert lang.contains(".") : "locale file is missmatch";

		option.setLang(new Locale(lang.replaceAll(".ini", "")));

		LockUtil.deleteAllLockFile();
		option.setLock(true);

		option.setDebugMode(debugMode.isSelected());

		setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void logFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logFileActionPerformed

		JFileChooser fileChooser = new JFileChooser(logFile.getText());
		fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() && f.canWrite() || (f.getName().toLowerCase().endsWith(".log") || f.getName().toLowerCase().endsWith(".txt"));
			}

			@Override
			public String getDescription() {
				return "テキストファイル(*.log,*.txt)";
			}
		});
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setMultiSelectionEnabled(false);
		int res = fileChooser.showSaveDialog(this);
		if (res != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File selected = fileChooser.getSelectedFile();
		if (selected == null) {
			return;
		}
		logFile.setText(selected.getAbsolutePath());


    }//GEN-LAST:event_logFileActionPerformed

    private void logFileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logFileMouseClicked
		logFileActionPerformed(null);
    }//GEN-LAST:event_logFileMouseClicked

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
		if (evt.getKeyCode() == Keys.ENTER.getKeyCode()) {
			jButton1.doClick();
		}
    }//GEN-LAST:event_jButton1KeyPressed

    private void windowSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowSizeActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_windowSizeActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(GameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(GameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(GameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(GameLauncher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>
		//</editor-fold>
		//</editor-fold>
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GameLauncher("").setVisible(true);
			}
		});
	}

	private void init(String name) {
		//アイコンの設定
		remove(jToolBar1);
		setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());

		//ログファイルのデフォルトパス記入
		logFile.setText(new File("KinugasaGame.log").getAbsolutePath());

		//翻訳ファイルの選択肢追加
		for (File f : new File("./translate/").listFiles(p -> p.getName().toLowerCase().endsWith(".ini"))) {
			language.addItem(f.getName());
		}

		//ウインドウ位置の変更
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		center.x -= getWidth() / 2;
		center.y -= getHeight() / 2;
		setLocation(center);

		//ウインドウサイズの初期選択
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		if (e.getMaximumWindowBounds().width > 1440 && e.getMaximumWindowBounds().height > 960) {
			windowSize.setSelectedIndex(2);
		} else if (e.getMaximumWindowBounds().width > 1080 && e.getMaximumWindowBounds().height > 720) {
			windowSize.setSelectedIndex(1);
		} else {
			windowSize.setSelectedIndex(0);
		}

		jButton1.grabFocus();

		//ウインドウタイトルの設定
		setTitle(name);
	}

	private GameOption option;

	public GameOption getGameOption() {
		return option;
	}

	public JCheckBox getMouse() {
		return mouse;
	}

	public JCheckBox getKeyboard() {
		return keyboard;
	}

	public JCheckBox getGamepad() {
		return gamepad;
	}

	public GameLauncher setMouse(boolean f) {
		mouse.setSelected(f);
		return this;
	}

	public GameLauncher setKeyboard(boolean f) {
		keyboard.setSelected(f);
		return this;
	}

	public GameLauncher setGamePad(boolean f) {
		gamepad.setSelected(f);
		return this;
	}

	public GameLauncher lockMouse() {
		mouse.setEnabled(false);
		return this;
	}

	public GameLauncher lockKeyboard() {
		keyboard.setEnabled(false);
		return this;
	}

	public GameLauncher lockGamePad() {
		gamepad.setEnabled(false);
		return this;
	}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField args;
    private javax.swing.JCheckBox debugMode;
    private javax.swing.JCheckBox gamepad;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JCheckBox keyboard;
    private javax.swing.JComboBox<String> language;
    private javax.swing.JTextField logFile;
    private javax.swing.JCheckBox mouse;
    private javax.swing.JComboBox<String> windowSize;
    // End of variables declaration//GEN-END:variables
}
