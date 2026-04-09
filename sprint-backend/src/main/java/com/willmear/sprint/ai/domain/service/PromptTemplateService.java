package com.willmear.sprint.ai.domain.service;

import com.willmear.sprint.ai.prompt.template.PromptTemplateCatalog;
import org.springframework.stereotype.Service;

@Service
public class PromptTemplateService {

    public String systemTemplate(String promptName) {
        return PromptTemplateCatalog.systemTemplate(promptName);
    }

    public String userTemplatePrefix(String promptName) {
        return PromptTemplateCatalog.userTemplatePrefix(promptName);
    }
}
