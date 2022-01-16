package cl.andres.streams.j8;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
            System.out.println(name + "'s salary raised in " + amount);
        }

        public double getSalary() {
            return salary;
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
            new Employee(3, "Mark Zuckerberg", 300000.0)//,
//            new Employee(4, "Another employee", 500000.0)
    };

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

    public static void streamForEach() {
        Stream.of(arrayOfEmps).forEach(employee -> employee.salaryIncrement(10.0));
    }

    public static void streamMap() {
        Integer[] empIds = { 1, 2, 3 };

        List<Employee> employees = Stream.of(empIds)
                .map(EmployeeRepository::findById)
                .collect(Collectors.toList());

        System.out.println("streamMap List of employees: ");
        System.out.println(employees);
    }

    public static void streamCollect() {
        List<Employee> employees = Stream.of(arrayOfEmps).collect(Collectors.toList());
        System.out.println("streamCollect List of employees: ");
        System.out.println(employees);
    }

    public static void streamFilter() {
        Integer[] empIds = { 1, 2, 3, 4 };

        List<Employee> employees = Stream.of(empIds)
                .map(EmployeeRepository::findById)
                .filter(Objects::nonNull)
                .filter(e -> e.getSalary() > 200000)
                .collect(Collectors.toList());

        System.out.println("Employees with salary greater than $200.000: ");
        System.out.println(employees);
    }
}
