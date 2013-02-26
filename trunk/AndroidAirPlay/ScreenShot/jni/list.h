#ifndef __LIST_H_
#define __LIST_H_

#define listoffsetof(TYPE, MEMBER) ((long) &((TYPE *)0)->MEMBER)

#define container_of(ptr, sample, member) \
    (void *)((char *)(ptr) - ((char *)&(sample)->member - (char *)(sample)))

#define LIST_HEAD_INIT(name) { &(name), &(name); }

struct list_head
{
    struct list_head *prev;
    struct list_head *next;
};

static inline void list_init_head(struct list_head *item)
{
    item->prev = item;
    item->next = item;
}

static inline void list_add(struct list_head *item, struct list_head *list)
{
    item->prev = list;
    item->next = list->next;
    list->next->prev = item;
    list->next = item;
}

static inline void list_add_tail(struct list_head *item, struct list_head *list)
{
    item->next = list;
    item->prev = list->prev;
    list->prev->next = item;
    list->prev = item;
}

static inline void list_replace(struct list_head *from, struct list_head *to)
{
    to->prev = from->prev;
    to->next = from->next;
    from->next->prev = to;
    from->prev->next = to;
}

static inline void list_del(struct list_head *item)
{
    item->prev->next = item->next;
    item->next->prev = item->prev;
}

static inline void list_del_init(struct list_head *item)
{
    item->prev->next = item->next;
    item->next->prev = item->prev;
    item->next = item;
    item->prev = item;
}

static inline int list_empty(struct list_head *item)
{
    return (item->next == item);
}

#define list_entry(__type, __item, __field)                     \
    ((__type *)(((char *)(__item)) - listoffsetof(__type, __field)))


#define list_for_each_entry(pos, head, member)                  \
   for (pos = container_of((head)->next, pos, member);          \
        &pos->member != (head);                                 \
        pos = container_of(pos->member.next, pos, member))

#define list_for_each_entry_safe(pos, storage, head, member)    \
   for (pos = container_of((head)->next, pos, member),          \
        storage = container_of(pos->member.next, pos, member);  \
        &pos->member != (head);                                 \
        pos = storage, storage = container_of(storage->member.next, storage, member))

#define list_for_each_entry_safe_rev(pos, storage, head, member)\
   for (pos = container_of((head)->prev, pos, member),          \
        storage = container_of(pos->member.prev, pos, member);  \
        &pos->member != (head);                                 \
        pos = storage, storage = container_of(storage->member.prev, storage, member))

#define list_for_each_entry_from(pos, start, head, member)      \
   for (pos = container_of((start), pos, member);               \
        &pos->member != (head);                                 \
        pos = container_of(pos->member.next, pos, member))

#define list_for_each_entry_from_rev(pos, start, head, member)  \
   for (pos = container_of((start), pos, member);               \
        &pos->member != (head);                                 \
        pos = container_of(pos->member.prev, pos, member))

#endif

