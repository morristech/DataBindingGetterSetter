package moe.xing.databindingformatter;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FiledDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<String> fileds;
    private PsiClass psiClass;
    private FiledDialog.OnConfirmListener listener;
    private DefaultListModel<String> model;
    private ArrayList<PsiField> mShowPsiFiledList = new ArrayList<>();

    public FiledDialog(PsiClass psiClass) {
        setContentPane(contentPane);
        setModal(true);
        this.psiClass = psiClass;
        model = new DefaultListModel<>();
        fileds.setModel(model);

        fileds.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setLocationRelativeTo(null);
        this.setTitle("select Filed To generator databinding getter setter");
    }

    private void onOK() {
        // add your code here
        if (listener != null) {
            ArrayList<PsiField> selectedList = new ArrayList<>();
            for (int index : fileds.getSelectedIndices()) {
                selectedList.add(mShowPsiFiledList.get(index));
            }
            listener.onConfirm(selectedList);
        }

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    void setData(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    void showDialog() {
        getRootPane().setDefaultButton(buttonOK);

        PsiField[] classFields = psiClass.getFields();

        model.clear();
        mShowPsiFiledList.clear();

        for (PsiField classField : classFields) {
            String filedName = classField.getName();
            Boolean alreadyHasGetter = false;
            for (PsiMethod method : psiClass.getMethods()) {
                if (method.getName().equals("get" + getFirstUpCaseName(filedName))) {
                    alreadyHasGetter = true;
                    break;
                }
            }
            if (!alreadyHasGetter) {
                model.addElement(filedName);
                mShowPsiFiledList.add(classField);
            }
        }

        this.pack();
        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2,
                (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        this.setVisible(true);
    }

    void setListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmListener {
        void onConfirm(ArrayList<PsiField> fields);
    }


    private String getFirstUpCaseName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
