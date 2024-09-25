SUMMARY = "Hardware drivers for ${MACHINE}"
SECTION = "base"
PRIORITY = "required"
LICENSE = "CLOSED"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PV = "${KV}+${SRCDATE}"

GCC = "6.3.0"

SRC_URI = "http://source.mynonpublic.com/xtrend/${MACHINE}-drivers-${KV}-${GCC}-${SRCDATE}.zip"

S = "${WORKDIR}"

INHIBIT_PACKAGE_STRIP = "1"

do_compile() {
}

do_populate_sysroot() {
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KV}/extra
    install -d ${D}${sysconfdir}/modules-load.d
    for i in tpm modloader modloader2 dvb; do
        install -m 0755 ${WORKDIR}/${i}.ko ${D}${nonarch_base_libdir}/modules/${KV}/extra/${i}.ko
        echo ${i} >> ${D}${sysconfdir}/modules-load.d/_${MACHINE}.conf
    done
}

FILES_${PN} += "${sysconfdir}/modules-load.d/_${MACHINE}.conf ${nonarch_base_libdir}/modules/${KV}/extra"
