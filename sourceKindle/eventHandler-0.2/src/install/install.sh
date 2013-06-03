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
FILECOUNT=4

if [ -d "/etc/upstart" ]; then
  cp event_handler.conf /etc/upstart/
else
  cp event_handler /etc/init.d/
  ln -s /etc/init.d/event_handler /etc/rc2.d/S99event_handler
fi

update_percent_complete_scaled 1

mkdir -p /usr/local/bin

update_percent_complete_scaled 1

cp eventHandler /usr/local/bin/eventHandler.sh

update_percent_complete_scaled 1

chmod 755 /usr/local/bin/eventHandler.sh

update_percent_complete_scaled 1

return 0
