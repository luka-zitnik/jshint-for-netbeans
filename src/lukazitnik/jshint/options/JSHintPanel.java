package lukazitnik.jshint.options;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.LifecycleManager;
import org.openide.awt.NotificationDisplayer;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

public final class JSHintPanel extends javax.swing.JPanel {

    private final JSHintOptionsPanelController controller;
    private final String defaultJSFile = InstalledFileLocator.getDefault().locate("jshint.js", "lukazitnik.jshint", false).getPath();
    private final JSFileVerifier jSFileVerifier = new JSFileVerifier();
    private final JSFileVersionDeducer jSFileVersionDeducer = new JSFileVersionDeducer();

    JSHintPanel(final JSHintOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        jSFileTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                anyUpdate(de);
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                anyUpdate(de);
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                anyUpdate(de);
            }

            private void anyUpdate(DocumentEvent de) {
                controller.changed(!jSFileTextField.getText().equals(defaultJSFile));
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jSFileLabel = new javax.swing.JLabel();
        jSFileTextField = new javax.swing.JTextField();
        defaultJSFileButton = new javax.swing.JButton();
        browseForJSFileButton = new javax.swing.JButton();
        jSFileInfo = new javax.swing.JLabel();
        showAnnotationsCheckBox = new javax.swing.JCheckBox();

        fileChooser.setFileFilter(new JSFilesOnlyFilter());

        setPreferredSize(new java.awt.Dimension(600, 58));

        org.openide.awt.Mnemonics.setLocalizedText(jSFileLabel, org.openide.util.NbBundle.getMessage(JSHintPanel.class, "JSHintPanel.jSFileLabel.text")); // NOI18N

        jSFileTextField.setInputVerifier(jSFileVerifier);

        org.openide.awt.Mnemonics.setLocalizedText(defaultJSFileButton, org.openide.util.NbBundle.getMessage(JSHintPanel.class, "JSHintPanel.defaultJSFileButton.text")); // NOI18N
        defaultJSFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultJSFileButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(browseForJSFileButton, org.openide.util.NbBundle.getMessage(JSHintPanel.class, "JSHintPanel.browseForJSFileButton.text")); // NOI18N
        browseForJSFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseForJSFileButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jSFileInfo, org.openide.util.NbBundle.getMessage(JSHintPanel.class, "JSHintPanel.jSFileInfo.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(showAnnotationsCheckBox, org.openide.util.NbBundle.getMessage(JSHintPanel.class, "JSHintPanel.showAnnotationsCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSFileLabel)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSFileInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                            .addComponent(jSFileTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseForJSFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultJSFileButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(showAnnotationsCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSFileLabel)
                    .addComponent(jSFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseForJSFileButton)
                    .addComponent(defaultJSFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSFileInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showAnnotationsCheckBox)
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void defaultJSFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultJSFileButtonActionPerformed
        jSFileTextField.setText(defaultJSFile);
    }//GEN-LAST:event_defaultJSFileButtonActionPerformed

    private void browseForJSFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseForJSFileButtonActionPerformed
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            jSFileTextField.setText(file.getPath());
        }
    }//GEN-LAST:event_browseForJSFileButtonActionPerformed

    void load() {
        Preferences p = NbPreferences.forModule(JSHintPanel.class);
        jSFileTextField.setText(p.get("jshint.js", defaultJSFile));
        showAnnotationsCheckBox.setSelected(p.getBoolean("show.annotations", true));
    }

    @NbBundle.Messages({
        "LBL_RestartRequest=JSHint plugin asks for restart",
        "DESC_RestartRequest=Your changes to the configuration of the plugin will take effect after a restart.",
        "ICON_RestartRequest="
    })
    void store() {
        Preferences p = NbPreferences.forModule(JSHintPanel.class);
        String oldJSFiile = p.get("jshint.js", defaultJSFile);
        String newJSFile = jSFileTextField.getText();

        if (!oldJSFiile.equals(newJSFile)) {
            NotificationDisplayer.getDefault().notify(Bundle.LBL_RestartRequest(), new ImageIcon(Bundle.ICON_RestartRequest()), Bundle.DESC_RestartRequest(), new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    LifecycleManager dlm = LifecycleManager.getDefault();
                    dlm.markForRestart();
                    dlm.exit();
                }
            });
        }

        p.put("jshint.js", newJSFile);
        p.putBoolean("show.annotations", showAnnotationsCheckBox.isSelected());
    }

    @NbBundle.Messages(
            "ERR_BadJSFile=The file doesn't look right."
    )
    boolean valid() {
        if (jSFileTextField.getText().isEmpty()) {
            jSFileInfo.setText(NbBundle.getMessage(JSHintPanel.class, "JSHintPanel.jSFileInfo.text"));
            jSFileInfo.setForeground(UIManager.getColor("Label.foreground"));
            return false;
        }

        boolean jSFileValid = jSFileVerifier.verify(jSFileTextField);

        if (jSFileValid) {
            jSFileInfo.setText("JSHint, version " + jSFileVersionDeducer.deduce(jSFileTextField));
            jSFileInfo.setForeground(UIManager.getColor("Label.foreground"));
        } else {
            jSFileInfo.setText(Bundle.ERR_BadJSFile());
            jSFileInfo.setForeground(Color.red);
        }

        return jSFileValid;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseForJSFileButton;
    private javax.swing.JButton defaultJSFileButton;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jSFileInfo;
    private javax.swing.JLabel jSFileLabel;
    private javax.swing.JTextField jSFileTextField;
    private javax.swing.JCheckBox showAnnotationsCheckBox;
    // End of variables declaration//GEN-END:variables
}
