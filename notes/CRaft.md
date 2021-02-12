# CRaft

[FAST '20 - CRaft: An Erasure-coding-supported Version of Raft for Reducing Storage Cost](https://www.youtube.com/watch?v=cbOb25x4HUY&feature=emb_logo)

## Main Idea

Using data fragments to reduce storage space and network overhead in a Raft cluster.
Note: The same technique can be used in Paxos.

## Novel Technique

- Variation of Raft using fragment replication. Requires F+k servers in happy case, full replication as normal Raft (F+1) when live servers > F+k.

## Claim

- increase in performance
- decrease in storage

## Summary

There are questions of:
liveness - we need 2N+1 followers for liveness in Raft in general (to make decisions/progress/accept writes)
How to perform fragment replication, how many servers needed?
They choose F+K. Why F+k?
First: use "Reed-Solomon" code (k,m), where k+m = N and k <= F+1 - every server gets a different fragment.
Data is comitted when F+k fragments are stored. Why?

Example:

```
Suppose 7 servers. F = 3, k = 3
  F+k = 6. 6 servers have fragments
___________
o o o o o o o
      _______
      F+1 = 4. For any future leader, there is k+1 fragment overlap. Any future leader will be able to recover the data
```

What if fewer than F+k servers?
No big deal, they proceed with Raft protocol as normal. Full replication when necessary (1gb is copied to all servers), otherwise (during F+k servers) send fragments.

Novel technique: fragment replication

## Questions

- Log replication is straightforward in vanilla Raft - we snapshot. How do we replicate the log if fragments are everywhere?
- Under what conditions do they assume increase in performance?
