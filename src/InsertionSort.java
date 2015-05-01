
public class InsertionSort {

	public static void main(String[] args) {
		
		int myArray[]={3,4,2,7,1};
		
		for(int j=1;j<myArray.length;j++)
		{
			int i=j-1;
			int key=myArray[j];
			
			while(i>=0 && myArray[i]>key)
			{
				myArray[i+1]=myArray[i];
				i=i-1;
				myArray[i+1]=key;
			}
		}
		
		for(int i:myArray)
		System.out.println(i);
	  }
	}
