///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.kohsuke:github-api:2.0.0-alpha-2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.15.2
//DEPS com.fasterxml.jackson.core:jackson-annotations:2.15.2
//DEPS com.fasterxml.jackson.core:jackson-core:2.15.2

import org.kohsuke.github.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitHubOrgImport {

    static record Resources(List<Resource> resources) {}
    static record Resource(String type, String name, String id) {}


    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: ./GitHubOrgImport.java <orgName> <githubToken>");
            System.exit(1);
        }

        String orgName = args[0];
        String githubToken = args[1];

        var importData = new HashMap<String, Object>();

        // Initialize GitHub client
        GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();

        // Get organization
        GHOrganization organization = github.getOrganization(orgName);

        List<Resource> resources = new ArrayList<>();
        for (GHRepository repo : organization.listRepositories()) {
            System.out.println("Processing repository: " + repo.getName());
            resources.add(new Resource("github:index/repository:Repository", repo.getName(), repo.getName()));

            repo.listLabels().forEach(label -> {
                resources.add(new Resource("github:index/issueLabels:IssueLabels", repo.getName() + "/" + label.getName(), label.getName()));
            });
        }

        importData.put("resources", resources);

        // Write to import.json
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("import.json"), importData);

        System.out.println("import.json has been generated successfully!");
    }
}
