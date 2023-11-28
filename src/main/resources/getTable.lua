---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by dell.
--- DateTime: 2023/2/16 15:46
---
local r
if (redis.call('hget', KEYS[3], ARGV[1])) then
    r = redis.pcall('hget', KEYS[3], ARGV[1])
else
    if (redis.call('exists', KEYS[1]) > 0) then
        local l = tonumber(redis.call('llen', KEYS[1]))
        local i = 0
        while (i < l) do
            local t = redis.call('lindex', KEYS[1], i)
            if (redis.call('sismember', KEYS[2], t) <= 0) then
                redis.call('sadd', KEYS[2], t)
                r = t
                i = l
            end
            i = i + 1
        end
        redis.call('hset', KEYS[3], ARGV[1], r)
    end
end
return r