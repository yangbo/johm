package redis.clients.johm.utils;

/**
 * Utility about Object.
 * Usage example {@link SimpleUser}.
 */
public class ObjectHelper {
	
	/**
	 * Helper function to compute hasCode by fields.
	 * 
	 * @param thisFields that used to count hash code.
	 * @return hash code computed from thisFields elements.
	 */
    public static int hashCode(Object[] thisFields) {
        final int prime = 31;
        int result = 1;

		for (int i=0; i<thisFields.length; i++){
			Object field = thisFields[i];
			if (field != null){
				result = prime * result + field.hashCode();
			}
		}
		return result;
	}
	
    /**
     * Helper function to determine equality.
     */
    public static boolean equals(Object[] thisFields, Object[] otherFields, Object thisObj, Object other) {
    	if (thisObj == other){
            return true;
    	}
        if (other == null){
            return false;
        }
        if (thisObj.getClass() != other.getClass()) {
            return false;
        }
        for (int i=0; i<thisFields.length; i++){
        	Object field = thisFields[i];
        	Object thatField = otherFields[i];
        	boolean isComparable = field instanceof Comparable<?>;
        	if (field == null) {
                if (thatField != null){
                    return false;
                }
            } else {
            	if (isComparable){
            		@SuppressWarnings("unchecked")
					Comparable<Object> a = (Comparable<Object>) field;
	            	if (a.compareTo(thatField)!=0){
	            		return false;
	            	}
            	} else if (!field.equals(thatField)){
            		return false;
            	}
            }
        }
        return true;
	}
}
