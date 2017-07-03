package moe.xing.databindingformatter;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.apache.http.util.TextUtils;

/**
 * Created by Qixingchen on 16-9-12.
 *
 * change class and write it.
 */
class WriterUtil extends WriteCommandAction.Simple {

    private PsiClass mClass;
    private PsiElementFactory mFactory;
    private Project mProject;
    private PsiFile mFile;
    private int[] selectedIndeces;

    WriterUtil(PsiFile mFile, Project project, PsiClass mClass, int[] selectedIndeces) {
        super(project, mFile);
        mFactory = JavaPsiFacade.getElementFactory(project);
        this.mFile = mFile;
        this.mProject = project;
        this.mClass = mClass;
        this.selectedIndeces = selectedIndeces;
    }

    @Override
    protected void run() throws Throwable {
        PsiField[] psiFields = mClass.getFields();
        for(int index: selectedIndeces) {
            addMethod(psiFields[index]);
        }
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        CodeStyleManager.getInstance(mProject).reformat(mClass);
    }

    private void addMethod(PsiField field) {

        String getter =
                "public " + field.getType().getPresentableText() + " get" + getFirstUpCaseName(field.getName()) +
                        "(){ \n" +
                        "return " + field.getName() + "; \n" +
                        "}";
        PsiMethod getMethod = mFactory.createMethodFromText(getter, mClass);
        getMethod.getModifierList().addAnnotation("android.databinding.Bindable");
        mClass.add(getMethod);

        String setter = "public void set" + getFirstUpCaseName(field.getName()) +
                "(" + field.getType().getPresentableText() + " " +
                field.getName() + "){\n " +
                "        this." + field.getName() + " = " + field.getName() + ";\n" +
                "        notifyPropertyChanged( BR." + field.getName() + ");\n" +
                "    }";
        mClass.add(mFactory.createMethodFromText(setter, mClass));
    }

    private String getFirstUpCaseName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
