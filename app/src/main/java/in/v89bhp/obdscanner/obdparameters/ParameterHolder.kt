package `in`.v89bhp.obdscanner.obdparameters

object ParameterHolder {
    private val _parameterList = mutableListOf<BaseParameter>()

    val parameterList: List<BaseParameter>
    get() = _parameterList

    private val _uploadParameterList = mutableListOf<BaseParameter>()

    val uploadParameterList
    get() = _uploadParameterList

    val parameterCount: Int
    get() = _parameterList.size

    val uploadParameterCount: Int
    get() = uploadParameterList.size

    var parameterAdded = false
    var parameterRemoved = false

    var uploadParameterAdded = false
    var uploadParameterRemoved = false

    fun addParameter(parameter: BaseParameter) {
        _parameterList.add(parameter)
        parameterAdded = true
    }

    fun removeParameter(parameter: BaseParameter) {
        _parameterList.remove(parameter)
        parameterRemoved = true
    }

    fun addUploadParameter(parameter: BaseParameter) {
        _uploadParameterList.add(parameter)
        uploadParameterAdded = true
    }

    fun removeUploadParameter(className: String) {
        _uploadParameterList.removeAll { it::class.java.name == className }
        uploadParameterRemoved = true
    }

    fun getParameterIndex(parameter: BaseParameter): Int =
        _parameterList.indexOf(parameter)

}