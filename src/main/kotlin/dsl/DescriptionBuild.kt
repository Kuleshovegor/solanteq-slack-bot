package dsl

import dsl.builders.DescriptionBuilder

@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class DescriptionDsl

fun description(descriptionBuilder: DescriptionBuilder.() -> Unit): BotConfig {
    return DescriptionBuilder().apply(descriptionBuilder).build()
}