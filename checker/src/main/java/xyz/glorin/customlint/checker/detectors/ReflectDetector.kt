package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReferenceExpression
import org.jetbrains.uast.UReferenceExpression
import java.util.*

/**
 * @author solli on 2020/3/25.
 */
class ReflectDetector : Detector(), UastScanner {
    override fun getApplicableReferenceNames(): List<String>? {
        return Arrays.asList(
            "ReflectUtil",
            "ReflectUtils"
        )
    }

    override fun visitReference(
        context: JavaContext,
        reference: UReferenceExpression,
        referenced: PsiElement
    ) {
        var node: PsiElement? = reference.javaPsi as? PsiReferenceExpression ?: return
        node = node?.parent
        if (node !is PsiReferenceExpression) {
            return
        }
        node = node.getParent()
        if (node !is PsiMethodCallExpression) {
            return
        }
        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "反射操作的类，请添加混淆规则，反射调用放入try...catch块中"
        )
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            "Reflect",
            "反射注意防混淆",
            "反射操作的类，请添加混淆规则，反射调用放入try...catch块中",
            LINT, 5, Severity.ERROR,
            Implementation(ReflectDetector::class.java, JAVA_FILE_SCOPE)
        )
    }
}