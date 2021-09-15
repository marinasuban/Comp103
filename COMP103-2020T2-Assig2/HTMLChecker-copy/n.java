import ecs100.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;

/**
 * Write a description of class n here.
 *
 * @author (your name)
 * @version (a version number or a date)
 *
 */
public class n
{
    public void mixup(List<String> letters , int start , int end){
if ( start <= end) {
int mid = start + ((end - start)/3);
UI. print ( letters . get(mid));
mixup( letters , mid+1, end);
mixup( letters , start , mid-1);
}
}
}
