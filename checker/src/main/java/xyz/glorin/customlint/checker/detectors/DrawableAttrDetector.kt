package xyz.glorin.customlint.checker.detectors

import com.android.SdkConstants
import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Scope.Companion.RESOURCE_FILE_SCOPE
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.*

/**
 * 检测 Drawable 资源问题
 */
class DrawableAttrDetector : ResourceXmlDetector() {
    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        return folderType == ResourceFolderType.DRAWABLE
    }

    override fun getApplicableElements(): Collection<String>? {
        return Arrays.asList(
            "solid", SdkConstants.TAG_ITEM
        )
    }

    override fun visitElement(context: XmlContext, element: Element) {
        val map = element.attributes
        val len = map.length
        for (i in 0 until len) {
            val node = map.item(i)
            if (node != null && ATTRS.contains(node.nodeName)) {
                val value = node.nodeValue
                if (value != null && value.startsWith("?")) {
                    showTip(context, node)
                    break
                }
            }
        }
    }

    companion object {
        //extends Detector implements Detector.XmlScanner {
        val ISSUE: Issue = Issue.create(
            "XMLUsage",
            "版本不兼容或不支持",
            "api21开始才支持自定义Drawable使用attr属性",
            LINT,
            5, Severity.ERROR,
            Implementation(
                DrawableAttrDetector::class.java,
                RESOURCE_FILE_SCOPE
            )
        )
        private val ATTRS = Arrays.asList(
            "android:color",
            "android:drawable"
        )

        private fun showTip(context: XmlContext, attributeNode: Node) {
            context.report(
                ISSUE,
                attributeNode,
                context.getLocation(attributeNode),
                "版本不兼容或不支持"
            )
        }
    }
}