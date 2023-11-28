if redis.call('exists', KEYS[1]) then
    if redis.call('llen', KEYS[1]) > 0 then
        return redis.call('lpop', KEYS[1], 1)
    else
        return {};
    end
else
    return {}
end