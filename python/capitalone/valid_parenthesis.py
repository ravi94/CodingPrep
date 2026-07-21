
def valid_par(str):
    stk = []
    map = {
        '(' : ')',
        '{' : '}',
        '[' : ']'
    }
    mapKeys = list(map.keys())
    mapValues = list(map.values())
    for ch in str:
        if (ch in mapKeys ) :
            stk.append(ch)
        elif ch in mapValues :
            if len(stk) == 0 or stk[-1] not in mapKeys: 
                return False
            elif mapValues.index(ch) != mapKeys.index(stk[-1]):
                return False
            else:
                stk.pop();
      
    
    if len(stk) != 0:
        return False
    else:
        return True

if __name__ == "__main__":
    str="())"
    print(valid_par(str))
