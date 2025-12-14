import java.io.*;
import java.util.*;

public class Sorting {
    public static void main(String[] args) throws IOException {
        String[] filenames = {
                "CW3_Data_Files/1000places_random.csv",
                "CW3_Data_Files/1000places_sorted.csv",
                "CW3_Data_Files/10000places_random.csv",
                "CW3_Data_Files/10000places_sorted.csv"

        };

        String[] algorithms = {"Insertion Sort", "Quick Sort", "Merge Sort"};

        long[][] times = new long[filenames.length][algorithms.length];

        for (int i = 0; i < filenames.length; i++) {
            List<String> originalData = readDataFromFile(filenames[i]);

            // Insertion Sort
            List<String> dataCopy = new ArrayList<>(originalData);

            long start = System.nanoTime();
            insertionSort(dataCopy);
            long end = System.nanoTime();
            times[i][0] = end - start;

            // Quick Sort
            dataCopy = new ArrayList<>(originalData);
            start = System.nanoTime();
            quickSort(dataCopy);
            end = System.nanoTime();
            times[i][1] = end - start;

            // Merge Sort
            dataCopy = new ArrayList<>(originalData);
            start = System.nanoTime();
            mergeSort(dataCopy);
            end = System.nanoTime();
            times[i][2] = end - start;
        }

        System.out.printf("%-25s %-20s %-20s %-20s%n", "Dataset", "Insertion Sort (ns)", "Quick Sort (ns)", "Merge Sort (ns)");
        for (int i = 0; i < filenames.length; i++) {
            System.out.printf("%-25s %-20d %-20d %-20d%n",
                    filenames[i],
                    times[i][0],
                    times[i][1],
                    times[i][2]);
        }
    }

    private static List<String> readDataFromFile(String filename) throws IOException {
        List<String> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            data.add(line.trim());
        }
        br.close();
        return data;
    }

    public static void insertionSort(List<String> list) {
        for (int i = 1; i < list.size(); i++) {
        /* insert list[i] into a sorted sublist list[0...i-1] so that
            list[0...i] is sorted. */
            String currentElement = list.get(i);
            int k;
            for (k = i - 1; k >= 0 && list.get(k).compareTo(currentElement)>0 ; k--) {
                list.set(k+1, list.get(k));
            }

            // Insert the current element into list[k+1]
            list.set(k+1, currentElement);
        }
    }

    /** The method for sorting the strings */
    public static void quickSort(List<String> list) {
        quickSort(list, 0, list.size() - 1);
    }

    private static void quickSort(List<String> list, int first, int last) {
        if (last > first) {
            int pivotIndex = partition(list, first, last);
            quickSort(list, first, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, last);
        }
    }

    /** Partition the list[first...last] */
    private static int partition(List<String> list, int first, int last) {
        // random create a index of pivot
        int pivotIndex = first + (int)(Math.random() * (last - first + 1));

        // change pivot to the first position
        Collections.swap(list, first, pivotIndex);

        String pivot = list.get(first); // Choose the first element as the pivot
        int low = first + 1; // Index for forward search
        int high = last; // Index for backward search

        while (high > low) {
            // Search forward from the left
            while (low <= high && list.get(low).compareTo(pivot) <= 0)
                low++;

            // Search backward from right
            while (low <= high && list.get(high).compareTo(pivot) > 0)
                high--;

            // Swap two elements in the list
            if (high > low) {
                Collections.swap(list, high, low);
            }
        }

        while (high > first && list.get(high).compareTo(pivot) >= 0)
            high--;

        // Swap pivot with list[high]
        if (pivot.compareTo(list.get(high)) > 0) {
            list.set(first, list.get(high));
            list.set(high, pivot);
            return high;
        } else {
            return first;
        }
    }


    // Merge Sort
    /** The method for sorting the numbers */
    public static void mergeSort(List<String> list) {
        if (list.size() > 1) {
            // Merge sort the first half
            List<String> firstHalf = new ArrayList<>(list.subList(0, list.size() / 2));
            mergeSort(firstHalf);

            // Merge sort the second half
            List<String> secondHalf = new ArrayList<>(list.subList(list.size() / 2, list.size()));
            mergeSort(secondHalf);

            // Merge firstHalf with secondHalf into the list
            merge(firstHalf, secondHalf, list);
        }
    }

    /** Merge two sorted lists */
    public static void merge(List<String> list1, List<String> list2, List<String> temp) {
        int current1 = 0; // Current index in list1
        int current2 = 0; // Current index in list2
        int current3 = 0; // Current index in temp

        while (current1 < list1.size() && current2 < list2.size()) {
            if (list1.get(current1).compareTo(list2.get(current2)) <= 0)
                temp.set(current3++, list1.get(current1++));
            else
                temp.set(current3++, list2.get(current2++));
        }

        while (current1 < list1.size())
            temp.set(current3++, list1.get(current1++));

        while (current2 < list2.size())
            temp.set(current3++, list2.get(current2++));
    }
}
