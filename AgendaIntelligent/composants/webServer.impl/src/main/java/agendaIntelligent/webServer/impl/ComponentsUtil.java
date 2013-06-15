package agendaIntelligent.webServer.impl;

import net.fortuna.ical4j.model.*;

import java.util.*;

/**
 * Util fonctions
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
public class ComponentsUtil {

	public static List<net.fortuna.ical4j.model.component.VEvent> sortByStartDate(net.fortuna.ical4j.model.Calendar calendar) {
	
		// Sorted list to return
		List<net.fortuna.ical4j.model.component.VEvent> sortedEvents = new ArrayList<net.fortuna.ical4j.model.component.VEvent>();
		// Events to sort
		net.fortuna.ical4j.model.ComponentList unsortedComponents = calendar.getComponents();
		
		// Current component to sort
		net.fortuna.ical4j.model.Component currentUnsortedComponent = null;
		// Current event to sort
		net.fortuna.ical4j.model.component.VEvent currentUnsortedEvent = null;
		// Current event already sort
		net.fortuna.ical4j.model.component.VEvent currentSortedEvent = null;
		
		// Iterator on unsorted events
		Iterator<net.fortuna.ical4j.model.Component> iteratorUnsorted = unsortedComponents.iterator();
		// Iterator on sorted events
		Iterator<net.fortuna.ical4j.model.component.VEvent> iteratorSorted = null;
		
		// Utils
		Boolean stop;
		Integer position;
		java.util.Date testedDate;
		java.util.Date sortedDate;
		
		// process on the unsorted list
		while(iteratorUnsorted.hasNext()) {
			currentUnsortedComponent = iteratorUnsorted.next();
			if(currentUnsortedComponent.getName().equals("VEVENT")) {
				currentUnsortedEvent = (net.fortuna.ical4j.model.component.VEvent) currentUnsortedComponent;
				testedDate = (java.util.Date) currentUnsortedEvent.getStartDate().getDate();
			
				position = 0;
				stop = false;
				iteratorSorted = sortedEvents.iterator();
				// process on the sorted list
				while((iteratorSorted.hasNext()) && !stop) {
					currentSortedEvent = iteratorSorted.next();
					sortedDate = (java.util.Date) currentSortedEvent.getStartDate().getDate();
					// test on the date
					if(testedDate.after(sortedDate)) {
						position++;
					} else {
						stop = true;
					}
				}
				// addition to the correct position
				sortedEvents.add(position, currentUnsortedEvent);
			}
		}
		return sortedEvents;
	}	
	
}
