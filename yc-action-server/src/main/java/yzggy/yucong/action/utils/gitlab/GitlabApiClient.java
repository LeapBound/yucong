package yzggy.yucong.action.utils.gitlab;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Namespace;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yamath
 * @since 2023/10/12 14:56
 */
public class GitlabApiClient {

    private static final Logger logger = LoggerFactory.getLogger(GitlabApiClient.class);

    private final static String HOST = "http://192.168.7.197:8083";
    private final static String ACCESS_TOKEN = "FkxF68GvtxDwEd5NyBfw";

    public static void getLocalProjectFiles(String groupName, String projectName, String branch, String srcPath, String groovyPath) { // rpa yc-action-server master
        try (GitLabApi gitLabApi = new GitLabApi(HOST, ACCESS_TOKEN)) {
            List<Namespace> namespaces = gitLabApi.getNamespaceApi().findNamespaces(groupName); // find namespace
            if (namespaces == null || namespaces.isEmpty()) {
                logger.error("Gitlab 没有找到项目, group = {}", groupName);
                return;
            }
            Project project = null;
            for (Namespace namespace : namespaces) {
                project = gitLabApi.getProjectApi().getProject(namespace.getName(), projectName); // find project
                if (!StrUtil.isEmptyIfStr(project)) {
                    break;
                }
            }
            if (project == null) {
                logger.error("Gitlab 没有找到项目, group = {}, project = {}", groupName, projectName);
                return;
            }
            //
            List<TreeItem> list = gitLabApi.getRepositoryApi().getTree(project.getId(), srcPath, branch); // find tree
            for (TreeItem item : list) {
                // file
                RepositoryFile repositoryFile = gitLabApi.getRepositoryFileApi().getFile(project.getId(), item.getPath(), branch, true);
                // new file
                if (!groovyPath.endsWith("/")) {
                    groovyPath = groovyPath.concat("/");
                }
                String localPath = groovyPath + item.getName();
                // write file
                FileUtil.writeUtf8String(repositoryFile.getDecodedContentAsString(), localPath);
            }
        } catch (Exception ex) {
            logger.error("Gitlab getLocalProjectFiles error, ", ex);
        }
    }
}
