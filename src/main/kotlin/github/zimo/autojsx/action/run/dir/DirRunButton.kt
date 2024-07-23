package github.zimo.autojsx.action.run.dir

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rd.util.getLogger
import com.jetbrains.rd.util.warn
import github.zimo.autojsx.action.news.NewAutoJSX
import github.zimo.autojsx.icons.ICONS
import github.zimo.autojsx.server.VertxCommandServer
import github.zimo.autojsx.util.*
import io.vertx.core.json.JsonObject
import java.io.File


/**
 * 运行当前项目
 */
class DirRunButton : AnAction(ICONS.START_16) {

    private val projectJSON = "project.json"
    private val targetFileName = "project.json"
    private val targetDirName = "resources"
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        runServer(project)
        val folder = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (folder?.isDirectory == true) {
//            runProject(folder, e.project)
            zipProject(folder,e.project){
                logI("预运行项目: " + it.projectJsonPath)
                logI("├──> 项目 src: " + it.srcPath)
                logI("├──> 项目 resources: " + it.resourcesPath)
                logI("└──> 项目 lib: " + it.libPath+"\r\n")
                VertxCommandServer.Command.runProject(it.zipPath)
            }
        }
    }

    @Deprecated("TODO")
    private fun runProject(file: VirtualFile, project: Project?) {
        val jsonFile = findFile(file, projectJSON)
        if (jsonFile != null) {
            val projectJson = File(jsonFile.path)
            val json = JsonObject(projectJson.readText())
            runServer(project)

            val name = json.getString("name")
            val src = projectJson.resolve(json.getString("srcPath")).canonicalFile
            //TODO 创建临时混淆目录，并混淆，如果开启了混淆
            val resources = projectJson.resolve(json.getString("resources")).canonicalFile
            val lib = projectJson.resolve(json.getString("lib")).canonicalFile

            val zip = File(project?.basePath + "/build-output" + "/${name}.zip")
            zip.parentFile.mkdirs()
            if (zip.exists()) zip.delete()

            executor.submit {
                zip(
                    arrayListOf(src.path, resources.path, lib.path),
                    project?.basePath + File.separator + "build-output" + File.separator + "${name}.zip"
                )
                VertxCommandServer.Command.runProject(zip.canonicalPath)
                logI("项目正在上传: " + projectJson.path)
                logI("正在上传 src: " + src.path)
                logI("项目正在上传 resources: " + resources.path)
                logI("项目正在上传 lib: " + lib.path)
                logI("项目上传完成" + "\r\n")
//                if (zip.exists()) zip.delete()

            }
            return
        }
        logE("项目无法上传: 选择的文件夹没有包含项目描述文件 'project.json'")
    }

    override fun update(e: AnActionEvent) {
        getLogger<NewAutoJSX>().warn { "The update method used a method marked as unstable" }
        e.presentation.isEnabledAndVisible = (e.project?.modules?.count { it.moduleTypeName == "AUTO_JSX_MODULE_TYPE" } ?: 0) > 0
    }
}