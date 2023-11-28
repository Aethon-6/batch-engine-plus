if (redis.call('hget', KEYS[1], ARGV[1])) then
    return redis.call('hget', KEYS[1], ARGV[1])
else
    redis.call('hset', KEYS[1], ARGV[1], ARGV[2])
end
return '-1'