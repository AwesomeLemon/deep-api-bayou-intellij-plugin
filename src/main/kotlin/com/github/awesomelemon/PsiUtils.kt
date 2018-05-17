package com.github.awesomelemon

import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.util.PsiUtil

object PsiUtils {
    fun createCodeBlock(code: String, project: Project): PsiCodeBlock {
        val parserFacade = JavaPsiFacade.getInstance(project).parserFacade
        return ApplicationManager.getApplication().runReadAction<PsiCodeBlock> {
            parserFacade.createCodeBlockFromText(code, null)
        }
    }

    fun createImportsShortenedBlock(qualifiedCode: String, project: Project): PsiCodeBlock {
        val parserFacade = JavaPsiFacade.getInstance(project).parserFacade
        val codeStyleManager = JavaCodeStyleManager.getInstance(project)
        val codeBlock = ApplicationManager.getApplication().runReadAction<PsiElement> {
            codeStyleManager.shortenClassReferences(parserFacade.createCodeBlockFromText(qualifiedCode, null))
        }

        return codeBlock as PsiCodeBlock
    }

    fun reformatFile(file: PsiFile, project: Project) {
        val processor = ReformatCodeProcessor(project, file, null, false)
        processor.runWithoutProgress()
    }

    fun executeWriteAction(project: Project, file: PsiFile, body: () -> Unit) {
        object : WriteCommandAction.Simple<Any>(project, file) {
            override fun run() {
                body()
            }
        }.execute()
    }
}

val PsiType.className: String?
    get() = PsiUtil.resolveClassInType(this)?.name

val PsiType.qualifiedClassName: String?
    get() = PsiUtil.resolveClassInType(this)?.qualifiedName


