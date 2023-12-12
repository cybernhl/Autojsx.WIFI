package github.zimo.autojsx.action.run.top

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import github.zimo.autojsx.icons.ICONS
import github.zimo.autojsx.server.VertxServer
import github.zimo.autojsx.util.*
import io.vertx.core.json.JsonObject
import java.io.File


/**
 * 运行当前项目
 */
class TopRunButton : AnAction(ICONS.START_16) {


    private val targetFileName = "project.json"
    private val targetDirName = "resources"
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        runServer(project)
        searchProjectJsonByEditor(project) { file ->
//            runProject(file, project)
            zipProject(file,e.project){
                VertxServer.Command.saveProject(it.zipPath)
                VertxServer.Command.runProject(it.zipPath)
                logI("项目正在上传: " + it.projectJsonPath)
                logI("正在上传 src: " + it.srcPath)
                logI("项目正在上传 resources: " + it.resourcesPath)
                logI("项目正在上传 lib: " + it.libPath+"\r\n")
            }
        }
    }

    @Deprecated("TODO")
    private fun runProject(file: VirtualFile, project: Project?) {
        val projectJson = File(file.path)
        val json = JsonObject(projectJson.readText())

        val name = json.getString("name")
        val src = projectJson.resolve(json.getString("srcPath")).canonicalFile
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
    //                ConsoleOutputV2.systemPrint("文件打包完成: "+project?.basePath+"/build-output"+"/${name}.zip")
            VertxServer.Command.runProject(zip.canonicalPath)
    //                if(zip.exists()) zip.delete()
        }
    }
}