import java.util.*;

public class ContactTracerBackup {

    private final int INIT_SIZE = 50;

    private PriorityQueue<Integer>[][] graph;

    private Map<String, Integer> personIndices;

    private List<Person> persons;

    /**
     * Initialises an empty ContactTracer with no populated contact traces.
     */
    public ContactTracerBackup() {
        // TODO: implement this!
        persons = new ArrayList<>();
        personIndices = new HashMap<>();
        graph = new PriorityQueue[INIT_SIZE][INIT_SIZE];
        initialiseGraph(graph);
    }

    private void initialiseGraph(PriorityQueue<Integer>[][] graph) {
        for (int i = 0; i < graph.length; ++i) {
            for (int j = 0; j < graph.length; ++j) {
                graph[i][j] = new PriorityQueue<>();
            }
        }
    }

    private void extendGraph() {
        int newSize = graph.length + INIT_SIZE;
        PriorityQueue<Integer>[][] newGraph = new PriorityQueue[newSize][newSize];
        initialiseGraph(newGraph);
        for (int i = 0; i < graph.length; ++i) {
            System.arraycopy(graph[i], 0, newGraph[i], 0, graph.length);
        }
        graph = newGraph;
    }

    /**
     * Initialises the ContactTracer and populates the internal data structures
     * with the given list of contract traces.
     *
     * @param traces to populate with
     * @require traces != null
     */
    public ContactTracerBackup(List<Trace> traces) {
        // TODO: implement this!
        this();
        for (Trace trace : traces) {
            addTrace(trace);
        }
    }

    /**
     * Adds a new contact trace to 
     * 
     * If a contact trace involving the same two people at the exact same time is
     * already stored, do nothing.
     * 
     * @param trace to add
     * @require trace != null
     */
    public void addTrace(Trace trace) {
        // TODO: implement this!
        if (persons.size() >= graph.length) {
            extendGraph();
        }
        String person1 = trace.getPerson1();
        String person2 = trace.getPerson2();
        int time = trace.getTime();
        Integer index1 = personIndices.get(person1);
        Integer index2 = personIndices.get(person2);
        if (index1 == null) {
            personIndices.put(person1, persons.size());
            persons.add(new Person(person1));
        }
        if (index2 == null) {
            personIndices.put(person2, persons.size());
            persons.add(new Person(person2));
        }
        index1 = personIndices.get(person1);
        index2 = personIndices.get(person2);
        if (!graph[index1][index2].contains(time)) {
            graph[index1][index2].add(time);
            graph[index2][index1].add(time);
        }
    }

    /**
     * Gets a list of times that person1 and person2 have come into direct 
     * contact (as per the tracing data).
     *
     * If the two people haven't come into contact before, an empty list is returned.
     * 
     * Otherwise the list should be sorted in ascending order.
     * 
     * @param person1 
     * @param person2
     * @return a list of contact times, in ascending order.
     * @require person1 != null && person2 != null
     */
    public List<Integer> getContactTimes(String person1, String person2) {
        // TODO: implement this!
        List<Integer> orderedTimes = new ArrayList<>();
        if (person1 == null || person2 == null) {
            return orderedTimes;
        }
        Integer index1 = personIndices.get(person1);
        Integer index2 = personIndices.get(person2);
        if (index1 == null || index2 == null) {
            return orderedTimes;
        }
        PriorityQueue<Integer> times = graph[index1][index2];
        Iterator<Integer> iterator = times.iterator();
        while (iterator.hasNext()) {
            orderedTimes.add(times.poll());
        }
        times.addAll(orderedTimes);

        return orderedTimes;
    }

    /**
     * Gets all the people that the given person has been in direct contact with
     * over the entire history of the tracing dataset.
     * 
     * @param person to list direct contacts of
     * @return set of the person's direct contacts
     */
    public Set<String> getContacts(String person) {
        // TODO: implement this!
        Set<String> names = new HashSet<>();
        Integer index = personIndices.get(person);
        if (index == null) {
            return names;
        }
        for (int i = 0; i < persons.size(); ++i) {
            if (!graph[index][i].isEmpty()) {
                names.add(persons.get(i).name);
            }
        }
        return names;
    }

    /**
     * Gets all the people that the given person has been in direct contact with
     * at OR after the given timestamp (i.e. inclusive).
     * 
     * @param person to list direct contacts of
     * @param timestamp to filter contacts being at or after
     * @return set of the person's direct contacts at or after the timestamp
     */
    public Set<String> getContactsAfter(String person, int timestamp) {
        // TODO: implement this!
        Set<String> names = new HashSet<>();
        Integer index = personIndices.get(person);
        PriorityQueue<Integer> times;
        if (index == null) {
            return names;
        }
        for (int i = 0; i < persons.size(); ++i) {
            times = graph[index][i];
            if (!times.isEmpty() && getInfected(times, timestamp)) {
                names.add(persons.get(i).name);
            }
        }
        return names;
    }

    /**
     * Determines whether a person will get infected from a certain person or not
     *
     * @param times a list of contact times
     * @param timestamp to filter contacts being at or after
     * @return true if there is at least one contact time being at or after timestamp
     *         false otherwise
     */
    private boolean getInfected(PriorityQueue<Integer> times, int timestamp) {
        for (Integer time : times) {
            if (time >= timestamp) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initiates a contact trace starting with the given person, who
     * became contagious at timeOfContagion.
     * 
     * Note that the return set shouldn't include the original person the trace started from.
     * 
     * @param person to start contact tracing from
     * @param timeOfContagion the exact time person became contagious
     * @return set of people who may have contracted the disease, originating from person
     */
    public Set<String> contactTrace(String person, int timeOfContagion) {
        // TODO: implement this!
        Set<String> contractedPersons = new HashSet<>();
        Stack<Person> stack = new Stack<>();
        Integer index = personIndices.get(person);
        if (index == null) {
            return contractedPersons;
        }
        setAllNotVisited();
        int contactTime = timeOfContagion; //- 60;
        Person contact;
        Person start = persons.get(index);
        start.visited = true;
        stack.push(start);
        while (!stack.empty()) {
            Person current = stack.peek();
            contact = getUnvisitedContact(current.name, contactTime);
            if (contact == null) {
                stack.pop();
            } else {
                contact.visited = true;
                stack.push(contact);
                contractedPersons.add(contact.name);
            }
        }
        return contractedPersons;
    }

    private Person getUnvisitedContact(String person, int timestamp) {
        Person contact;
        for (String name : getContactsAfter(person, timestamp)) {
            contact = persons.get(personIndices.get(name));
            if (!contact.visited) {
                return contact;
            }
        }
        return null;
    }

    /**
     * Sets all persons' visited attributes as not visited (false)
     */
    private void setAllNotVisited() {
        for (int i = 0; i < persons.size(); ++i) {
            persons.get(i).visited = false;
        }
    }

    private class Person {
        private final String name;
        private boolean visited;

        private Person(String name) {
            this.name = name;
            this.visited = false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj instanceof ContactTracerBackup.Person) {
                ContactTracerBackup.Person newObj = (ContactTracerBackup.Person)obj;
                return newObj.name.equals(this.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }
}
