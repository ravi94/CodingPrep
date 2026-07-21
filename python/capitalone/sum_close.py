
def sum_close_zero(arr):
    size = len(arr)
    
    if(size == 0):
        return -1;
    arr.sort();
    
    left = 0
    right = size-1
    min = float('inf')

    # print(arr,left ,right)

    while(left < right):
        sum = arr[left] + arr[right]
        if sum == 0:
            print("b",arr[left] , arr[right] )
            return 0
        elif (sum < 0): 
            left+=1
        else:
            right-=1  
        min = min if abs(sum)>min else abs(sum)    
    # print("a",res)
    return min

if __name__ == "__main__":
    arr= [7, -5, 2, -8, 6]
    print(sum_close_zero(arr))