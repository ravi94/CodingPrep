"""
Two Sum
-------
Given an array of integers `nums` and an integer `target`, return the indices
of the two numbers such that they add up to `target`.

Assume exactly one solution exists and the same element is not used twice.

Example:
    nums = [2, 7, 11, 15], target = 9  ->  [0, 1]   (nums[0] + nums[1] == 9)
"""
from typing import List


def two_sum(nums: List[int], target: int) -> List[int]:
    """O(n) time, O(n) space using a value -> index map."""
    seen = {}  # value -> index
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []  # no pair found


def _run_tests() -> None:
    assert two_sum([2, 7, 11, 15], 9) == [0, 1]
    assert two_sum([3, 2, 4], 6) == [1, 2]
    assert two_sum([3, 3], 6) == [0, 1]
    assert two_sum([1, 2, 3], 100) == []
    print("two_sum: all tests passed")


if __name__ == "__main__":
    _run_tests()
