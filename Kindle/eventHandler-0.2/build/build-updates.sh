#!/bin/bash
#
# $Id: build-updates.sh 8763 2012-10-25 18:30:16Z NiLuJe $
#

HACKNAME="eventHandler"
PKGNAME="${HACKNAME}"
PKGVER="0.2"

# We need kindletool (https://github.com/NiLuJe/KindleTool) in $PATH
if [ "$(./kindletool version | wc -l)" == "       1" ]; then
    	HAS_KINDLETOOL="true"
fi

if [ "${HAS_KINDLETOOL}" != "true" ]; then
	echo "You need KindleTool (https://github.com/NiLuJe/KindleTool) to build this package."
	exit 1
fi

# We also need GNU tar
TAR_BIN="tar"
if ! ${TAR_BIN} --version | grep "GNU tar" > /dev/null 2>&1 ; then
	echo "You need GNU tar to build this package."
	exit 1
fi

## Install
# Create the utils tarball
${TAR_BIN} --owner root --group root -cvzf utils.tar.gz consts ui

# Copy the script to our working directory, to avoid storing crappy paths in the update package
cp -f utils.tar.gz ../src/install/
cp -f utils.tar.gz ../src/uninstall/

# Build the install packages
./kindletool create ota2 -d kindle4 -C ../src/install update_${PKGNAME}_${PKGVER}_kindle4_install.bin
./kindletool create ota2 -d kindle5 -C ../src/install update_${PKGNAME}_${PKGVER}_kindle5_install.bin
# and uninstall ones
./kindletool create ota2 -d kindle4 -C ../src/uninstall update_${PKGNAME}_${PKGVER}_kindle4_uninstall.bin
./kindletool create ota2 -d kindle5 -C ../src/uninstall update_${PKGNAME}_${PKGVER}_kindle5_uninstall.bin

# Remove package specific temp stuff
rm -f ../src/install/utils.tar.gz ../src/uninstall/utils.tar.gz utils.tar.gz

# Move our update
mv -f *.bin ../
