package minerepo.analisys;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Filter<T> {

    private List<Predicate<T>> filters = new ArrayList<>();
    
    public List<Predicate<T>> getFilters(){
        return filters;
    }

    /**
     * Applies all filters
     * @param target to filter
     * @return false if at least one filter evaluates to false, else true
     */
    public boolean filter(T target){
        for(var filter : filters){
            if(!filter.test(target)){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a List with filtered elements
     * ofr each element the filter function will be called, if it evaluates to True, that target is added to the return list
     * @param targets List with elements to filter
     * @return ArrayList with filtered elements
     */
    public List<T> filterAll(List<T> targets){
        List<T> newList = new ArrayList<>();
        for(var t : targets){
            if(filter(t)){
                newList.add(t);
            }
        }
        return newList;
    }

}