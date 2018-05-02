package com.github.awesomelemon.bayou

/* author: tanvd */

object BayouTextConverter {
    private val import = "import edu.rice.cs.caper.bayou.annotations.Evidence;"

    private val classDeclaration = "class Main {"

    private val functionDeclaration = "void uniqueFunction"

    fun toProgramText(request: BayouRequest): String {
        return """
                    ${import}

                    ${classDeclaration}
                        ${functionDeclaration}${request.inputParameters.joinToString(prefix = "(", postfix = ")") { "${it.klass} ${it.name}" }} {

                            ${request.apiCallSequence.apiMethods.joinToString(separator = "\n") { "Evidence.apicalls(\"$it\");" }}
                            ${request.apiCallSequence.apiTypes.joinToString(separator = "\n") { "Evidence.types(\"$it\");" }}
                        }
                    }
                """
    }

    fun fromProgramText(text: String): BayouResponse {
        val space = "[\\n\\r\\s]"
        val startBracket = "$space*\\{$space*"
        val endBracket = "$space*\\}$space*"
        val importsText = Regex(".*${Regex.escape(import)}$space*(.*)$space*${Regex.escape(classDeclaration)}.*",
                setOf(RegexOption.DOT_MATCHES_ALL)).find(text)!!.groups[1]!!.value
        val imports: MutableList<String> = ArrayList()
        var matchResult = Regex("$space*import (.*);$space*").find(importsText)
        while (matchResult != null) {
            imports.add(matchResult.first())
            matchResult = matchResult.next()
        }
        val functionParamsGroup = "$space*\\([^\\(\\)]*\\)$space*"
        val code = Regex(".*${functionDeclaration}$functionParamsGroup$startBracket(.*)$endBracket$endBracket",
                setOf(RegexOption.DOT_MATCHES_ALL)).find(text)!!.first()
        return BayouResponse(imports, code)
    }

    fun MatchResult.first(): String {
        return groups[1]!!.value
    }

}