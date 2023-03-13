import java.util.*;

public class ErdosNumbers {
    /**
     * String representing Paul Erdos's name to check against
     */
    public static final String ERDOS = "Paul Erdös";

    /**
     * A list used to store all of the authors
     */
    private List<Author> authors;

    /**
     * A map used to store authors' names and their corresponding indices stored
     * in List<Author> authors
     */
    private Map<String, Integer> authorsIndices;

    /**
     * A map used to store paper titles and their corresponding authors' names
     */
    private Map<String, String[]> papersEntries;

    /**
     * A graph using adjacency matrix representation to represent co-authorship
     */
    private double[][] graph;

    /**
     * Total number of all authors
     */
    private int numAuthors;

    /**
     * Initialises the class with a list of papers and authors.
     *
     * Each element in 'papers' corresponds to a String of the form:
     * 
     * [paper name]:[author1][|author2[|...]]]
     *
     * Note that for this constructor and the below methods, authors and papers
     * are unique (i.e. there can't be multiple authors or papers with the exact same name or title).
     * 
     * @param papers List of papers and their authors
     */
    public ErdosNumbers(List<String> papers) {
        authors = new ArrayList<>();
        authorsIndices = new HashMap<>();
        papersEntries = new HashMap<>();
        String[] works, writers;
        Author author;
        int index;
        for (String entry : papers) {
            works = entry.split(":|\\|");
            writers = new String[works.length - 1];
            for (int i = 1; i < works.length; ++i) {
                writers[i - 1] = works[i];
                author = new Author(works[i]);
                index = authors.indexOf(author);
                if (index == -1) {
                    author.addPaper(works[0]);
                    authorsIndices.put(works[i], authors.size());
                    authors.add(author);
                } else {
                    authors.get(index).addPaper(works[0]);
                }
            }
            papersEntries.put(works[0], writers);
        }
        numAuthors = authors.size();

        // generates co-authorship graph
        graph = new double[numAuthors][numAuthors];
        for (int i = 0; i < numAuthors; ++i) {
            for (int j = 0; j < numAuthors; ++j) {
                graph[i][j] = Double.MAX_VALUE;
            }
        }
        for (String[] names : papersEntries.values()) {
            for (int i = 0; i < names.length - 1; ++i) {
                for (int j = i + 1; j < names.length; ++j) {
                    addCoAuthorship(names[i], names[j]);
                }
            }
        }
    }

    /**
     * Gets all the unique papers the author has written (either solely or
     * as a co-author).
     *
     * @param author to get the papers for.
     * @return the unique set of papers this author has written.
     */
    public Set<String> getPapers(String author) {
        return authors.get(authorsIndices.get(author)).getPapers();
    }

    /**
     * Gets all the unique co-authors the author has written a paper with.
     *
     * @param author to get collaborators for
     * @return the unique co-authors the author has written with.
     */
    public Set<String> getCollaborators(String author) {
        Set<String> coAuthors = new HashSet<>();
        Integer index = authorsIndices.get(author);
        if (index == null) {
            return coAuthors;
        }
        for (int i = 0; i < numAuthors; ++i) {
            if (graph[index][i] != Double.MAX_VALUE) {
                coAuthors.add(authors.get(i).getName());
            }
        }
        return coAuthors;
    }

    /**
     * Checks if Erdos is connected to all other author's given as input to
     * the class constructor.
     *
     * In other words, does every author in the dataset have an Erdos number?
     *
     * @return the connectivity of Erdos to all other authors.
     */
    public boolean isErdosConnectedToAll() {
        return retrieveAllCoauthors() == numAuthors;
    }

    /**
     * Calculate the Erdos number of an author.
     *
     * This is defined as the length of the shortest path on a graph of paper
     * collaborations (as explained in the assignment specification).
     *
     * If the author isn't connected to Erdos (and in other words, doesn't have
     * a defined Erdos number), returns Integer.MAX_VALUE.
     *
     * Note: Erdos himself has an Erdos number of 0.
     *
     * @param author to calculate the Erdos number of
     * @return authors' Erdos number or otherwise Integer.MAX_VALUE
     */
    public int calculateErdosNumber(String author) {
        if (author.equals(ERDOS)) {
            return 0;
        }
        setErdosNumbersToMaxInteger();
        retrieveAllCoauthors();
        return authors.get(authorsIndices.get(author)).erdosNumber;
    }

    /**
     * Gets the average Erdos number of all the authors on a paper.
     * If a paper has just a single author, this is just the author's Erdos number.
     *
     * Note: Erdos himself has an Erdos number of 0.
     *
     * @param paper to calculate it for
     * @return average Erdos number of paper's authors
     */
    public double averageErdosNumber(String paper) {
        setErdosNumbersToMaxInteger();
        retrieveAllCoauthors();
        String[] writers = papersEntries.get(paper);
        double erdosNumSum = 0;
        for (String writer : writers) {
            erdosNumSum += authors.get(authorsIndices.get(writer)).erdosNumber;
        }
        return erdosNumSum / writers.length;
    }

    /**
     * Calculates and gets the "weighted Erdos number" of an author.
     *
     * If the author isn't connected to Erdos (and in other words, doesn't have
     * an Erdos number), returns Double.MAX_VALUE.
     *
     * Note: Erdos himself has a weighted Erdos number of 0.
     *
     * @param author to calculate it for
     * @return author's weighted Erdos number
     */
    public double calculateWeightedErdosNumber(String author) {
        if (author.equals(ERDOS)) {
            return 0;
        }
        if (!authorsIndices.containsKey(author)) {
            return Double.MAX_VALUE;
        }
        // finding shortest path using Dijkstra algorithm
        setAllNotVisited();
        setWeightedErdosNumsToMaxDouble();
        ArrayDeque<Author> dest = new ArrayDeque<>();
        Author start = authors.get(authorsIndices.get(ERDOS));
        Author goal = authors.get(authorsIndices.get(author));
        start.visited = true;
        start.weightedErdosNum = 0;
        dest.add(start);

        Set<String> names;
        Author parent, child, minimum;
        int count = 1;
        double newErdosNum;
        while (count++ < numAuthors) {
            parent = dest.getLast();
            names = getCollaborators(parent.getName());
            for (String name : names) {
                child = authors.get(authorsIndices.get(name));
                if (!child.visited) {
                    newErdosNum = parent.weightedErdosNum
                            + getEdgeWeight(parent, child);
                    if (newErdosNum < child.weightedErdosNum) {
                        child.weightedErdosNum = newErdosNum;
                    }
                }
            }
            minimum = getAuthorVsMinWeightedErdosNum();
            if (minimum == null) { // disjointed trees
                break;
            }
            if (minimum.equals(goal)) {
                return minimum.weightedErdosNum;
            }
            minimum.visited = true;
            dest.add(minimum);
        }
        return Double.MAX_VALUE;
    }

    /**
     * Adds two given authors as co-authorship
     *
     * @param nameA a given author's name
     * @param nameB the other given author's name
     */
    private void addCoAuthorship(String nameA, String nameB) {
        int indexA = authorsIndices.get(nameA);
        int indexB = authorsIndices.get(nameB);
        Author authorA = authors.get(indexA);
        Author authorB = authors.get(indexB);
        double edgeWeight = 1.0 / getNumOfCollaborativePapers(authorA, authorB);
        graph[indexA][indexB] = edgeWeight;
        graph[indexB][indexA] = edgeWeight;
    }

    /**
     * Retrieves all connected authors using BFS, assigns their corresponding
     * erdosNumbers, and calculates the total number of all connected authors
     *
     * @return the total number of all connected authors
     */
    private int retrieveAllCoauthors() {
        setAllNotVisited();
        Queue<Author> queue = new ArrayDeque<>();
        Author start = authors.get(authorsIndices.get(ERDOS));
        start.visited = true;
        start.erdosNumber = 0;
        queue.add(start);

        int coAuthorCount = 0;
        Author parent, child;
        Set<String> names;
        while (!queue.isEmpty()) {
            parent = queue.remove();
            ++coAuthorCount;
            names = getCollaborators(parent.getName());
            for (String name : names) {
                child = authors.get(authorsIndices.get(name));
                if (!child.visited) {
                    child.visited = true;
                    child.erdosNumber = parent.erdosNumber + 1;
                    queue.add(child);
                }
            }
        }
        return coAuthorCount;
    }

    /**
     * Sets all authors' visited attributes as not visited (false)
     */
    private void setAllNotVisited() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).visited = false;
        }
    }

    /**
     * Sets all authors' erdosNumber attributes to Integer.MAX_VALUE
     */
    private void setErdosNumbersToMaxInteger() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).erdosNumber = Integer.MAX_VALUE;
        }
    }

    /**
     * Sets all authors' weightedErdosNum attributes to Double.MAX_VALUE
     */
    private void setWeightedErdosNumsToMaxDouble() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).weightedErdosNum = Double.MAX_VALUE;
        }
    }

    /**
     * Gets the number of books jointly written by the given two authors
     *
     * @param a a given author
     * @param b the other given author
     * @return the number of books jointly written by the two authors
     */
    private int getNumOfCollaborativePapers(Author a, Author b) {
        int count = 0;
        Iterator<String> iterator = a.papers.iterator();
        while (iterator.hasNext()) {
            if (b.papers.contains(iterator.next())) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Gets the edge weight number between the given two authors
     *
     * @param a a given author
     * @param b the other given author
     * @return the edge weight number between the two authors
     */
    private double getEdgeWeight(Author a, Author b) {
        int indexA = authorsIndices.get(a.getName());
        int indexB = authorsIndices.get(b.getName());
        return graph[indexA][indexB];
    }

    /**
     * A utility function to find the author with minimum weighted erdos number,
     * from the list of authors not yet visited
     *
     * @return the author with minimum weighted erdos number
     */
    private Author getAuthorVsMinWeightedErdosNum() {
        int authorIndex = -1;
        double minErdosNum = Double.MAX_VALUE;
        Author author;
        for (int i = 0; i < numAuthors; ++i) {
            author = authors.get(i);
            if (!author.visited && author.weightedErdosNum < minErdosNum) {
                authorIndex = i;
                minErdosNum = author.weightedErdosNum;
            }
        }
        if (authorIndex == -1) {
            return null;
        }
        return authors.get(authorIndex);
    }

    /**
     * A class represents author
     */
    private class Author {
        // the name of author
        private final String name;

        // the value of erdos number
        private int erdosNumber;

        // the value of weighted erdos number
        private double weightedErdosNum;

        // visiting log of this author
        private boolean visited;

        // a set to store all the papers written by this author
        private Set<String> papers;

        /**
         * Constructs a new instance of Author with the given name
         * has initial erdosNumber value of Integer.MAX_VALUE and
         * initial weightedErdosNum value of Double.MAX_VALUE
         *
         * @param name the name of author to be constructed
         */
        private Author(String name) {
            this.name = name;
            this.erdosNumber = Integer.MAX_VALUE;
            this.weightedErdosNum = Double.MAX_VALUE;
            this.visited = false;
            this.papers = new HashSet<>();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj instanceof Author) {
                Author newObj = (Author)obj;
                return newObj.getName().equals(this.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }


        /**
         * Gets the name of this author
         *
         * @return the name of this author
         */
        private String getName() {
            return name;
        }

        /**
         * Gets the set of papers written by this author
         *
         * @return the set of papers written by this author
         */
        private Set<String> getPapers() {
            return papers;
        }

        /**
         * Adds the given paper to the set of papers belongs to this author
         *
         * @param paper the paper to be added
         */
        private void addPaper(String paper) {
            papers.add(paper);
        }
    }
}
