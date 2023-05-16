package `in`.v89bhp.obdscanner.obdparameters

object SupportedPidsHolder {

    data class EcuSupportedPids(val serviceId: String, val ecuId: String, val supportedPids: Set<String>)

    /**
     * Contains supported PIDs ECU-wise
     */
    val ecuList = mutableListOf<EcuSupportedPids>()

    fun contains(pid: String): Boolean {
        // The list has not been populated yet
        if(ecuList.isEmpty()) return true

        for(ecu in ecuList) {
            if(ecu.supportedPids.contains(pid))
                return true
        }
        return false
    }

    fun clear() {
        ecuList.clear()
    }
}