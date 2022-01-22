package cl.andres.streams.j8;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamsJ8 {
    /**
     *
     * @param args in arguments
     */
    public static void main(String[] args)  {
        streamForEach();
        streamMap();
        streamCollect();
        streamFilter();
        streamFindFirst();
        streamToArray();
        streamFlatMap();
        streamPeek();
        streamInfinite();
        streamSort();
        streamMinMax();
        streamDistinct();
        streamAllMatchAnyMatchNoneMatch();
        specializedStreamsCreation();
        streamAverage();
        streamReduce();
        streamAdvancedCollect();
        streamParallel();
        streamGenerate();
        streamIterate();
        streamFileWrite();
        streamFileRead();
    }

    public static class Employee {
        private Integer id;
        private String name;
        private double salary;

        public Employee(Integer id, String name, double salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        public void salaryIncrement(double amount) {
            this.salary = this.salary + ((this.salary * amount) / 100);
            System.out.println(name + "'s salary raised in " + amount + "%");
        }

        public double getSalary() {
            return salary;
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", wage=" + salary +
                    '}';
        }
    }

    public static Employee[] arrayOfEmps = {
            new Employee(1, "Jeff Bezos", 100000.0),
            new Employee(2, "Bill Gates", 200000.0),
            new Employee(3, "Mark Zuckerberg", 300000.0)
    };

    public static final String FILE_PATH = "file.txt";

    public static class EmployeeRepository {
        public static Employee findById(Integer id) {

            for (Employee employee : arrayOfEmps) {

                if (employee.id == id) {
                    return employee;
                }
            }

            return null;
        }
    }

    /**
     * Few simple examples of stream creation.
     */
    public static void someWaysToCreateStreams() {

        // Option 1: using Stream class
        Stream.of(arrayOfEmps);
        // Creates Stream from individual objects
        Stream.of(arrayOfEmps[0], arrayOfEmps[1], arrayOfEmps[2]);

        // Option 2: using Arrays class
        List<Employee> empList = Arrays.asList(arrayOfEmps);
        empList.stream();

        // Option 3: using Stream builder
        Stream.Builder<Employee> empStreamBuilder = Stream.builder();
        empStreamBuilder.accept(arrayOfEmps[0]);
        empStreamBuilder.accept(arrayOfEmps[1]);
        empStreamBuilder.accept(arrayOfEmps[2]);
        Stream<Employee> empStream = empStreamBuilder.build();
    }

    /**
     * forEach
     *
     * forEach() is simplest and most common operation; it loops over the stream elements, calling the supplied
     * function on each element.
     *
     * The method is so common that is has been introduced directly in Iterable, Map etc.
     *
     */
    public static void streamForEach() {
        System.out.println("streamForEach");
        System.out.println("========================================================================================");

        Stream.of(arrayOfEmps).forEach(employee -> employee.salaryIncrement(10.0));

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * This will effectively call the salaryIncrement() on each element in the empList.
         *
         * forEach() is a terminal operation, which means that, after the operation is performed, the stream pipeline
         * is considered consumed, and can no longer be used.
         */
    }

    /**
     * map
     *
     * map() produces a new stream after applying a function to each element of the original stream. The new stream
     * could be of different type.
     *
     * The following example converts the stream of Integers into the stream of Employees.
     */
    public static void streamMap() {
        System.out.println("streamMap");
        System.out.println("========================================================================================");

        Integer[] empIds = { 1, 2, 3 };

        List<Employee> employees = Stream.of(empIds)
                .map(EmployeeRepository::findById)
                .collect(Collectors.toList());

        System.out.println("streamMap List of employees: ");
        System.out.println(employees);

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * Here, we obtain an Integer stream of employee ids from an array. Each Integer is passed to the function
         * employeeRepository::findById() – which returns the corresponding Employee object; this effectively forms an
         * Employee stream.
         */
    }

    /**
     * collect
     *
     * We saw how collect() works in the previous example; its one of the common ways to get stuff out of the stream
     * once we are done with all the processing.
     */
    public static void streamCollect() {
        System.out.println("streamCollect");
        System.out.println("========================================================================================");

        List<Employee> employees = Stream.of(arrayOfEmps).collect(Collectors.toList());
        System.out.println("streamCollect List of employees: ");
        System.out.println(employees);

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * collect() performs mutable fold operations (repackaging elements to some data structures and applying some
         * additional logic, concatenating them, etc.) on data elements held in the Stream instance.
         *
         * The strategy for this operation is provided via the Collector interface implementation. In the example above,
         * we used the toList collector to collect all Stream elements into a List instance.
         */
    }

    /**
     * filter
     *
     * This produces a new stream that contains elements of the original stream that pass a given test (specified by a
     * Predicate).
     */
    public static void streamFilter() {
        System.out.println("streamFilter");
        System.out.println("========================================================================================");

        Integer[] empIds = { 1, 2, 3, 4 };

        List<Employee> employees = Stream.of(empIds)
                .map(EmployeeRepository::findById)
                .filter(Objects::nonNull)
                .filter(e -> e.getSalary() > 200000)
                .collect(Collectors.toList());

        System.out.println("Employees with salary greater than $200.000: ");
        System.out.println(employees);

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * In the example above, we first filter out null references for invalid employee ids and then again apply a
         * filter to only keep employees with salaries over a certain threshold.
         */
    }

    /**
     * findFirst
     *
     * findFirst() returns an Optional for the first entry in the stream; the Optional can, of course, be empty.
     */
    public static void streamFindFirst() {
        System.out.println("streamFindFirst");
        System.out.println("========================================================================================");

        Integer[] empIds = { 1, 2, 3, 4 };

        Employee employee = Stream.of(empIds)
                .map(EmployeeRepository::findById)
                .filter(e -> e != null)
                .filter(e -> e.getSalary() > 100000)
                .findFirst()
                .orElse(null);

        System.out.println("The first employee found with salary greater than $100.000: " + employee);

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * Here, the first employee with the salary greater than 100000 is returned. If no such employee exists, then
         * null is returned.
         */
    }

    /**
     * toArray
     *
     * We saw how we used collect() to get data out of the stream. If we need to get an array out of the stream, we can
     * simply use toArray().
     */
    public static void streamToArray(){
        System.out.println("streamToArray");
        System.out.println("========================================================================================");

        Employee[] employees = Arrays.stream(arrayOfEmps).toArray(Employee[]::new);

        System.out.println("Employees to array: " + employees);

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * The syntax Employee[]::new creates an empty array of Employee – which is then filled with elements from
         * the stream.
         */
    }

    /**
     * flatMap
     *
     * A stream can hold complex data structures like Stream<List<String>>. In cases like this, flatMap() helps us to
     * flatten the data structure to simplify further operations.
     */
    public static void streamFlatMap() {
        System.out.println("streamFlatMap");
        System.out.println("========================================================================================");

        List<List<String>> namesNested = Arrays.asList(
                Arrays.asList("Jeff", "Bezos"),
                Arrays.asList("Bill", "Gates"),
                Arrays.asList("Mark", "Zuckerberg"));

        List<String> namesFlatStream = namesNested
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        System.out.println("Complex List flattened to simple List of String: " + namesFlatStream);

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * Notice how we were able to convert the Stream<List<String>> to a simpler Stream<String> – using the
         * flatMap() API.
         */
    }

    /**
     * peek
     *
     * We saw forEach() earlier in this section, which is a terminal operation. However, sometimes we need to perform
     * multiple operations on each element of the stream before any terminal operation is applied.
     *
     * peek() can be useful in situations like this. Simply put, it performs the specified operation on each element
     * of the stream and returns a new stream which can be used further. peek() is an intermediate operation.
     */
    public static void streamPeek() {

        System.out.println("streamPeek");
        System.out.println("========================================================================================");

        List<Employee> empList = Arrays.asList(arrayOfEmps);

        empList.stream()
                .peek(e -> e.salaryIncrement(10.0))
                .peek(System.out::println)
                .collect(Collectors.toList());

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * Here, the first peek() is used to increment the salary of each employee. The second peek() is used to print
         * the employees. Finally, collect() is used as the terminal operation.
         */
    }

    /**
     * Infinite Stream
     *
     * Some operations are deemed short-circuiting operations. Short-circuiting operations allow computations on
     * infinite streams to complete in finite time.
     */
    public static void streamInfinite() {
        System.out.println("streamInfinite");
        System.out.println("========================================================================================");

        Stream<Integer> infiniteStream = Stream.iterate(2, i -> i * 2);

        List<Integer> collect = infiniteStream
                .skip(3)
                .limit(5)
                .collect(Collectors.toList());

        System.out.println("Stream infinito reducido a: " + collect);

        System.out.println("========================================================================================");
        System.out.println("");

        /**
         * Here, we use short-circuiting operations skip() to skip first 3 elements, and limit() to limit to 5 elements
         * from the infinite stream generated using iterate().
         */
    }

    /**
     * sorted
     *
     * This sorts the stream elements based on the comparator passed we pass into it.
     */
    public static void streamSort() {
        System.out.println("streamInfinite");
        System.out.println("========================================================================================");

        List<Employee> employees = Arrays.stream(arrayOfEmps)
                .sorted((e1, e2) -> e1.getName().compareTo(e2.getName()))
                .collect(Collectors.toList());

        System.out.println("Empleados ordenados por nombre: ");
        System.out.println(employees);
        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * min and max
     *
     * As the name suggests, min() and max() return the minimum and maximum element in the stream respectively, based
     * on a comparator. They return an Optional since a result may or may not exist (due to, say, filtering).
     */
    public static void streamMinMax() {
        System.out.println("streamMinMax");
        System.out.println("========================================================================================");

        Employee firstEmp = Arrays.stream(arrayOfEmps)
                .min((e1, e2) -> e1.getId() - e2.getId())
                .orElseThrow(NoSuchElementException::new);

        System.out.println("Empleado con ID mínimo: " + firstEmp);

        /**
         * We can also avoid defining the comparison logic by using Comparator.comparing().
         */
        Employee maxSalEmp = Arrays.stream(arrayOfEmps)
                .max(Comparator.comparing(Employee::getSalary))
                .orElseThrow(NoSuchElementException::new);

        System.out.println("Empleado con salario máximo: " + maxSalEmp);
        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * distinct
     *
     * distinct() does not take any argument and returns the distinct elements in the stream, eliminating duplicates.
     * It uses the equals() method of the elements to decide whether two elements are equal or not
     */
    public static void streamDistinct() {
        System.out.println("streamDistinct");
        System.out.println("========================================================================================");

        List<Integer> intList = Arrays.asList(2, 5, 3, 2, 4, 3);
        List<Integer> distinctList = intList
                .stream()
                .distinct()
                .collect(Collectors.toList());

        System.out.println("distinctList: " + distinctList);
        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * allMatch, anyMatch, and noneMatch
     *
     * These operations all take a predicate and return a boolean. Short-circuiting is applied and processing is
     * stopped as soon as the answer is determined.
     *
     * allMatch: checks if the predicate is true for all the elements in the stream. Here, it returns false as soon as
     * it encounters 5, which is not divisible by 2.
     *
     * anyMatch: checks if the predicate is true for any one element in the stream. Here, again short-circuiting is
     * applied and true is returned immediately after the first element.
     *
     * noneMatch: checks if there are no elements matching the predicate. Here, it simply returns false as soon as it
     * encounters 6, which is divisible by 3.
     */
    public static void streamAllMatchAnyMatchNoneMatch() {
        System.out.println("streamAllMatchAnyMatchNoneMatch");
        System.out.println("========================================================================================");

        List<Integer> intList = Arrays.asList(2, 4, 5, 6, 8);

        boolean allEven = intList.stream().allMatch(i -> i % 2 == 0);
        boolean oneEven = intList.stream().anyMatch(i -> i % 2 == 0);
        boolean noneMultipleOfThree = intList.stream().noneMatch(i -> i % 3 == 0);

        System.out.println("allEven: " + allEven);
        System.out.println("oneEven: " + oneEven);
        System.out.println("noneMultipleOfThree: " + noneMultipleOfThree);
        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * =================================================================================================================
     * Specialized Streams
     *
     * There are also the IntStream, LongStream, and DoubleStream – which are primitive specializations for int, long
     * and double respectively. These are quite convenient when dealing with a lot of numerical primitives.
     *
     * Not all operations supported by Stream are present in these stream implementations. For example, the standard
     * min() and max() take a comparator, whereas the specialized streams do not.
     *
     * =================================================================================================================
     */

    /**
     * Creation
     */
    public static void specializedStreamsCreation() {
        System.out.println("specializedStreamsCreation");
        System.out.println("========================================================================================");

        /**
         * The most common way of creating an IntStream is to call mapToInt() on an existing stream.
         * Here, we start with a Stream<Employee> and get an IntStream by supplying the Employee::getId to mapToInt.
         * Finally, we call max() which returns the highest integer.
         */
        Integer latestEmpId = Arrays.stream(arrayOfEmps)
                .mapToInt(Employee::getId)
                .max()
                .orElseThrow(NoSuchElementException::new);

        System.out.println("ID de último empleado: " + latestEmpId);

        /**
         * We can also use IntStream.of() for creating the IntStream
         * or
         * IntStream.range() which creates IntStream of numbers 10 to 19.
         */
        IntStream intStream = IntStream.of(1, 2, 3);
        IntStream intStream2 = IntStream.range(10, 20);

        /**
         * One important distinction to note is that Stream.of () returns a Stream<Integer> and not IntStream.
         *
         * Similarly, using map() instead of mapToInt() returns a Stream<Integer> and not an IntStream.
         * empList.stream().map(Employee::getId);
         */

        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * =================================================================================================================
     *
     * Specialized Operations
     *
     * =================================================================================================================
     */

    /**
     * average
     */
    public static void streamAverage() {
        System.out.println("streamAverage");
        System.out.println("========================================================================================");

        Double avgSal = Arrays.stream(arrayOfEmps)
                .mapToDouble(Employee::getSalary)
                .average()
                .orElseThrow(NoSuchElementException::new);

        System.out.println("El promedio salarial es: " + avgSal);
        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * =================================================================================================================
     * Reduction Operations
     *
     * A reduction operation (also called as fold) takes a sequence of input elements and combines them into a single
     * summary result by repeated application of a combining operation. We already saw few reduction operations like
     * findFirst(), min() and max().
     *
     * =================================================================================================================
     */

    /**
     * reduce
     *
     * The most common form of reduce() is:
     *
     * T reduce(T identity, BinaryOperator<T> accumulator)
     *
     * where identity is the starting value and accumulator is the binary operation we repeated apply.
     */
    public static void streamReduce() {
        System.out.println("streamReduce");
        System.out.println("========================================================================================");

        /**
         * Here, we start with the initial value of 0 and repeated apply Double::sum() on elements of the stream.
         * Effectively we’ve implemented the DoubleStream.sum() by applying reduce() on Stream.
         */
        Double sumSal = Arrays.stream(arrayOfEmps)
                .map(Employee::getSalary)
                .reduce(0.0, Double::sum);

        System.out.println("Suma de todos los salarios: " + sumSal);
        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * =================================================================================================================
     *
     * Advanced collect
     *
     * =================================================================================================================
     */
    public static void streamAdvancedCollect() {
        System.out.println("streamAdvancedCollect");
        System.out.println("========================================================================================");

        /**
         * joining
         *
         * Collectors.joining() will insert the delimiter between the two String elements of the stream. It internally uses
         * a java.util.StringJoiner to perform the joining operation.
         */
        String empNames = Arrays.stream(arrayOfEmps)
                .map(Employee::getName)
                .collect(Collectors.joining(", "));
        System.out.println("Empleados: " + empNames);

        /**
         * toSet
         *
         * To get a set out of stream elements:
         */
        Set<String> empNamesSet = Arrays.stream(arrayOfEmps)
                .map(Employee::getName)
                .collect(Collectors.toSet());

        /**
         * toCollection
         *
         * We can use Collectors.toCollection() to extract the elements into any other collection by passing in a
         * Supplier<Collection>. We can also use a constructor reference for the Supplier.
         */
        Vector<String> empNamesCollection = Arrays.stream(arrayOfEmps)
                .map(Employee::getName)
                .collect(Collectors.toCollection(Vector::new));

        /**
         * summarizingDouble
         *
         * Another interesting collector – which applies a double-producing mapping function to each input element and
         * returns a special class containing statistical information for the resulting values.
         *
         * Notice how we can analyze the salary of each employee and get statistical information on that data – such as
         * min, max, average etc.
         */
        DoubleSummaryStatistics stats = Arrays.stream(arrayOfEmps)
                .collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.println("Estadísticas de los salarios: " + stats.toString());

        /**
         * summaryStatistics
         *
         * Can be used to generate similar result when we’re using one of the specialized streams.
         */
        DoubleSummaryStatistics stats2 = Arrays.stream(arrayOfEmps)
                .mapToDouble(Employee::getSalary)
                .summaryStatistics();
        System.out.println("Estadísticas de los salarios (2): " + stats2.toString());

        /**
         * partitioningBy
         *
         * We can partition a stream into two – based on whether the elements satisfy certain criteria or not.
         *
         * Let’s split our List of numerical data, into even and ods.
         * Here, the stream is partitioned into a Map, with even and odds stored as true and false keys.
         *
         */
        List<Integer> intList = Arrays.asList(2, 4, 5, 6, 8);
        Map<Boolean, List<Integer>> isEven = intList
                .stream()
                .collect(Collectors.partitioningBy(i -> i % 2 == 0));
        System.out.print("Números pares: "+ isEven.get(true).toString()
                + " - Números impares: " + isEven.get(false).toString() + "\n");

        /**
         * groupingBy
         *
         * Offers advanced partitioning – where we can partition the stream into more than just two groups.
         *
         * It takes a classification function as its parameter. This classification function is applied to each element
         * of the stream.
         *
         * The value returned by the function is used as a key to the map that we get from the groupingBy collector.
         *
         * In this quick example, we grouped the employees based on the initial character of their first name.
         */
        Map<Character, List<Employee>> groupByAlphabet = Arrays.stream(arrayOfEmps)
                .collect(Collectors.groupingBy(e -> new Character(e.getName().charAt(0))));
        System.out.println("Empleados agrupados por inicial: " + groupByAlphabet.toString());

        /**
         * mapping
         *
         * Sometimes we might need to group data into a type other than the element type.
         *
         * Here’s how we can do that; we can use mapping() which can actually adapt the collector to a different type –
         * using a mapping function.
         *
         * Here mapping() maps the stream element Employee into just the employee id – which is an Integer – using the
         * getId() mapping function. These ids are still grouped based on the initial character of employee first name.
         */
        Map<Character, List<Integer>> idGroupedByAlphabet = Arrays.stream(arrayOfEmps)
                .collect(Collectors.groupingBy(e -> new Character(e.getName().charAt(0)),
                        Collectors.mapping(Employee::getId, Collectors.toList())));
        System.out.println("Empleados agrupados por inicial y con sus IDs respectivos: " + idGroupedByAlphabet.toString());

        /**
         * reducing
         *
         * reducing() is similar to reduce(). It simply returns a collector which performs a reduction of its input
         * elements.
         *
         * Here reducing() gets the salary increment of each employee and returns the sum.
         *
         * reducing() is most useful when used in a multi-level reduction, downstream of groupingBy() or
         * partitioningBy(). To perform a simple reduction on a stream, use reduce() instead.
         */
        Double percentage = 10.0;
        Double salIncrOverhead = Arrays.stream(arrayOfEmps)
                .collect(Collectors.reducing(0.0, e -> e.getSalary() * percentage / 100, (s1, s2) -> s1 + s2));
        System.out.println("Gasto general en aumento de salario: " + salIncrOverhead);

        /**
         * Example of how we can use reducing() with groupingBy().
         *
         * Here we group the employees based on the initial character of their first name. Within each group, we find
         * the employee with the longest name.
         */
        Comparator<Employee> byNameLength = Comparator.comparing(Employee::getName);

        Map<Character, Optional<Employee>> longestNameByAlphabet = Arrays.stream(arrayOfEmps)
                .collect(Collectors.groupingBy(e -> new Character(e.getName().charAt(0)),
                        Collectors.reducing(BinaryOperator.maxBy(byNameLength))));
        System.out.println("Nombre más largo agrupado por inicial: " + longestNameByAlphabet);

        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * =================================================================================================================
     *
     * Parallel Streams
     *
     * =================================================================================================================
     */

    /**
     * parallel
     *
     * Using the support for parallel streams, we can perform stream operations in parallel without having to write any
     * boilerplate code; we just have to designate the stream as parallel.
     *
     * Here salaryIncrement() would get executed in parallel on multiple elements of the stream, by simply adding the
     * parallel() syntax.
     *
     * This functionality can, of course, be tuned and configured further, if you need more control over the
     * performance characteristics of the operation.
     *
     * As is the case with writing multi-threaded code, we need to be aware of few things while using parallel streams.
     *
     * We need to ensure that the code is thread-safe. Special care needs to be taken if the operations performed in
     * parallel modifies shared data.
     * We should not use parallel streams if the order in which operations are performed or the order returned in the
     * output stream matters. For example operations like findFirst() may generate the different result in case of
     * parallel streams.
     * Also, we should ensure that it is worth making the code execute in parallel. Understanding the performance
     * characteristics of the operation in particular, but also of the system as a whole – is naturally very important
     * here.
     */
    public static void streamParallel() {
        System.out.println("streamParallel");
        System.out.println("========================================================================================");

        List<Employee> empList = Arrays.asList(arrayOfEmps);
        empList.stream().parallel().forEach(e -> e.salaryIncrement(10.0));

        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * =================================================================================================================
     *
     * Infinite Streams
     *
     * =================================================================================================================
     */

    /**
     * generate
     *
     * We provide a Supplier to generate() which gets called whenever new stream elements need to be generated.
     *
     * With infinite streams, we need to provide a condition to eventually terminate the processing. One common way of
     * doing this is using limit(). In below example, we limit the stream to 5 random numbers and print them as they
     * get generated.
     *
     * Please note that the Supplier passed to generate() could be stateful and such stream may not produce the same
     * result when used in parallel.
     */
    public static void streamGenerate() {
        System.out.println("streamGenerate");
        System.out.println("========================================================================================");

        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);

        System.out.println("========================================================================================");
        System.out.println("");
    }
    /**
     * iterate
     *
     * iterate() takes two parameters: an initial value, called seed element and a function which generates next
     * element using the previous value. iterate(), by design, is stateful and hence may not be useful in parallel
     * streams.
     *
     * Here, we pass 2 as the seed value, which becomes the first element of our stream. This value is passed as input
     * to the lambda, which returns 4. This value, in turn, is passed as input in the next iteration.
     *
     * This continues until we generate the number of elements specified by limit() which acts as the terminating
     * condition.
     */
    public static void streamIterate() {
        System.out.println("streamIterate");
        System.out.println("========================================================================================");

        Stream<Integer> evenNumStream = Stream.iterate(2, i -> i * 2);

        List<Integer> collect = evenNumStream
                .limit(5)
                .collect(Collectors.toList());

        System.out.println("List of numbers: " + collect.toString());
        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * =================================================================================================================
     *
     * File Operations
     *
     * =================================================================================================================
     */

    /**
     * File Write Operation
     *
     * Here we use forEach() to write each element of the stream into the file by calling PrintWriter.println().
     */
    public static void streamFileWrite() {
        System.out.println("streamFileWrite");
        System.out.println("========================================================================================");

        String[] words = {
                "hello",
                "refer",
                "world",
                "level",
                "another"
        };

        try (PrintWriter pw = new PrintWriter(
                Files.newBufferedWriter(Paths.get(FILE_PATH)))) {

            Stream.of(words).forEach(pw::println);

            System.out.println("File successfully written!!!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("========================================================================================");
        System.out.println("");
    }

    /**
     * File Read Operation
     *
     * Here Files.lines() returns the lines from the file as a Stream which is consumed by the getPalindrome() for
     * further processing.
     *
     * getPalindrome() works on the stream, completely unaware of how the stream was generated. This also increases
     * code reusability and simplifies unit testing.
     */
    public static void streamFileRead() {
        System.out.println("streamFileRead");
        System.out.println("========================================================================================");

        try {
            List<String> str = getPalindrome(Files.lines(Paths.get(FILE_PATH)), 5);
            System.out.println("File read, palindrome words: " + str);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("========================================================================================");
        System.out.println("");
    }

    public static List<String> getPalindrome(Stream<String> stream, int length) {
        return stream
                .filter(s -> s.length() == length)
                .filter(s -> s.compareToIgnoreCase(new StringBuilder(s).reverse().toString()) == 0)
                .collect(Collectors.toList());
    }

}
