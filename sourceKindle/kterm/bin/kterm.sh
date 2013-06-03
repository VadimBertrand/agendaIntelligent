#!/bin/sh
EXTENSION=/mnt/us/extensions/kterm
PARAM=""
#WIDTH=`xwininfo -root|grep Width|cut -d ':' -f 2`
# use wider keyboard layout for screen width greater than 600 px
#if [ ${WIDTH} -gt 600 ]; then
#  PARAM="-l ${EXTENSION}/layouts/keyboard-wide.xml"
#fi
${EXTENSION}/bin/kterm ${PARAM}
# when started from launcher kterm doesn't kill keyboard (why?) so:
killall matchbox-keyboard
