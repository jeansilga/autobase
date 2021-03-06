
withConfig(configuration) {
    inline(phase: 'CONVERSION') { source, context, classNode ->
        classNode.putNodeMetaData('projectVersion', '4.0.0-SNAPSHOT')
        classNode.putNodeMetaData('projectName', 'autobase')
        classNode.putNodeMetaData('isPlugin', 'true')
    }
}
