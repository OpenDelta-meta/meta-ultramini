SUMMARY = "Linux kernel for ${MACHINE}"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/linux-${PV}/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "^(et7000mini|xpeedc)$"

inherit kernel machine_kernel_pr samba_change_dialect

SRC_URI[md5sum] = "e7ba35d427bfa40d78cd6e23db7872a2"
SRC_URI[sha256sum] = "88f648e462e9d37c6ed9401b33ee1dd08495e9f66b9c653aefd9fd0a4f5afb26"

# By default, kernel.bbclass modifies package names to allow multiple kernels
# to be installed in parallel. We revert this change and rprovide the versioned
# package names instead, to allow only one kernel to be installed.
PKG_${KERNEL_PACKAGE_NAME}-base = "${KERNEL_PACKAGE_NAME}-base"
PKG_${KERNEL_PACKAGE_NAME}-image = "${KERNEL_PACKAGE_NAME}-image"
RPROVIDES_${KERNEL_PACKAGE_NAME}-base = "${KERNEL_PACKAGE_NAME}-${KERNEL_VERSION}"
RPROVIDES_${KERNEL_PACKAGE_NAME}-image = "${KERNEL_PACKAGE_NAME}-image-${KERNEL_VERSION}"

SRC_URI = "http://source.mynonpublic.com/xtrend/linux-${PV}.tar.xz \
	file://defconfig \
	file://${OPENVISION_BASE}/meta-openvision/recipes-linux/kernel-patches/kernel-add-support-for-gcc${VISIONGCCVERSION}.patch \
	file://0001-regmap-add-regmap_write_bits.patch \
	file://0002-af9035-fix-device-order-in-ID-list.patch \
	file://0003-Add-support-for-dvb-usb-stick-Hauppauge-WinTV-soloHD.patch \
	file://0004-af9035-add-USB-ID-07ca-0337-AVerMedia-HD-Volar-A867.patch \
	file://0005-Add-support-for-EVOLVEO-XtraTV-stick.patch \
	file://0006-dib8000-Add-support-for-Mygica-Geniatech-S2870.patch \
	file://0007-dib0700-add-USB-ID-for-another-STK8096-PVR-ref-desig.patch \
	file://0008-add-Hama-Hybrid-DVB-T-Stick-support.patch \
	file://0009-Add-Terratec-H7-Revision-4-to-DVBSky-driver.patch \
	file://0010-media-Added-support-for-the-TerraTec-T1-DVB-T-USB-tu.patch \
	file://0011-media-tda18250-support-for-new-silicon-tuner.patch \
	file://0012-media-dib0700-add-support-for-Xbox-One-Digital-TV-Tu.patch \
	file://0013-mn88472-Fix-possible-leak-in-mn88472_init.patch \
	file://0014-staging-media-Remove-unneeded-parentheses.patch \
	file://0015-staging-media-mn88472-simplify-NULL-tests.patch \
	file://0016-mn88472-fix-typo.patch \
	file://0017-mn88472-finalize-driver.patch \
	file://0001-Support-TBS-USB-drivers-for-4.1-kernel.patch \
	file://0001-TBS-fixes-for-4.1-kernel.patch \
	file://0001-STV-Add-PLS-support.patch \
	file://0001-STV-Add-SNR-Signal-report-parameters.patch \
	file://blindscan2.patch \
	file://0001-stv090x-optimized-TS-sync-control.patch \
	file://0002-log2-give-up-on-gcc-constant-optimizations.patch \
	file://0003-makefile-disable-warnings.patch \
	file://0004-cp1emu-do-not-use-bools-for-arithmetic.patch \
	"

S = "${WORKDIR}/linux-${PV}"
B = "${WORKDIR}/build"

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_OUTPUT = "vmlinux"
KERNEL_IMAGETYPE = "vmlinux"
KERNEL_IMAGEDEST = "tmp"

FILES_${KERNEL_PACKAGE_NAME}-image = "/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz"

kernel_do_install_append() {
    ${STRIP} ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
    gzip -9c ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} > ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
    rm ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
}

pkg_postinst_${KERNEL_PACKAGE_NAME}-image () {
    if [ "x$D" == "x" ]; then
        if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz ] ; then
            flash_erase /dev/${MTD_KERNEL} 0 0
            nandwrite -p /dev/${MTD_KERNEL} /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
            rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
        fi
    fi
    true
}

do_rm_work() {
}

# extra tasks
addtask kernel_link_images after do_compile before do_install
