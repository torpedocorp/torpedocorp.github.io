package kr.co.bizframe.comparator;

import java.util.Comparator;
import org.apache.camel.component.file.GenericFile;

public class MyFileSorter<T> implements Comparator<GenericFile<T>>{
	
	@Override
    public int compare(GenericFile<T> f1, GenericFile<T> f2) {
					
		// 1. f1과 f2 fileName 비교하여 return
		return f1.getFileName().compareTo(f2.getFileName());
		
		// 2. custom method를 통해 원하는 부분만 추출하여 비교
        //String fileDate1 = getDateFromFilename(f1.getFileName());
        //String fileDate2 = getDateFromFilename(f2.getFileName());
        //return fileDate1.compareTo(fileDate2);
    }

	// fileName 중 원하는 부분만 추출
    private String getDateFromFilename(String fileName) {
        return fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
    }
}