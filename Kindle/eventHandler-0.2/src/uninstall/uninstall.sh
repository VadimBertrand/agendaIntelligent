#!/bin/sh
# Pull some helper functions for logging
source /etc/upstart/functions

[ -f utils.tar.gz ] && {
	tar xvf utils.tar.gz
	rm -f utils.tar.gz
}

# Pull some helper functions for progress bar handling
source consts/patchconsts
source ui/blanket
source ui/progressbar

LOG_DOMAIN=eventHandler
SCALING=1
FILECOUNT=3

if [ -d "/etc/upstart" ]; then
  rm /etc/upstart/event_handler.conf
else
  rm /etc/init.d/event_handler
  rm /etc/rc2.d/S99event_handler
fi

update_percent_complete_scaled 1

rm /usr/local/bin/eventHandler.sh

update_percent_complete_scaled 1

rmdir --ignore-fail-on-non-empty /usr/local/bin

update_percent_complete_scaled 1

return 0
