

def neighbour_sum(arr):
    res = []
    for i,item in enumerate(arr):
        left = 0 if i==0 else arr[i-1]
        right= 0 if i== len(arr)-1 else arr[i+1]
        # print("i --> ",i)
        res.append(left+ right+ arr[i])
    return res

if __name__ ==  "__main__":
    arr= [4, 0, 1, -2, 3]
    print(neighbour_sum(arr))