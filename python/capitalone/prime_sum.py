
def prime_sum(n):
    composite = [False] * (n+1)

    for i in range(2,n+1):
        if not composite[i]:
            for j in range(i*i , n+1, i):
                composite[j] = True

            
    return sum([i for i,item in enumerate(composite) if not composite[i]])

if __name__ == "__main__":
    print(prime_sum(17))