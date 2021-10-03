package com.example.pokeman.utilities

enum class Generation(val generationName: String) {
    GEN1("generation-i"),
    GEN2("generation-ii"),
    GEN3("generation-iii"),
    GEN4("generation-iv"),
    GEN5("generation-v"),
    GEN6("generation-vi"),
    GEN7("generation-vii"),
    GEN8("generation-viii");

    fun getTotalPokemon(): Int {
        val (start, end) = this.getStartAndEnd()
        return (end - start + 1)
    }

    fun getStartAndEnd(): Pair<Int, Int> {
        return when (this) {
            GEN1 -> Pair(1, 151)
            GEN2 -> Pair(152, 251)
            GEN3 -> Pair(252, 386)
            GEN4 -> Pair(387, 493)
            GEN5 -> Pair(494, 649)
            GEN6 -> Pair(650, 721)
            GEN7 -> Pair(722, 809)
            GEN8 -> Pair(810, 898)
        }
    }
}